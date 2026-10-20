package com.adielcalixto.ifacademico.data.session

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.local.SessionPreferences
import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.remote.dto.LoginDTO
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.NetworkError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.Session
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.Response
import java.io.IOException
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/** The only writer of the encrypted session and the only authority for account lifetimes. */
@Singleton
class SessionCoordinator @Inject constructor(
    private val preferences: SessionPreferences,
    @param:Named("raw") private val api: AcademicoAPI,
    private val cache: CacheService,
    private val clock: SessionClock,
    val freshness: AcademicFreshness,
    @param:Named("academicUrl") private val baseUrl: String,
    @param:Named("sessionScope") private val scope: CoroutineScope,
) {
    private val writes = Mutex()
    private val guard = Any()
    private val _state = MutableStateFlow(SessionSnapshot())
    val state = _state.asStateFlow()
    private var record = Session()
    private var restored = false
    private var generation = 0L
    private var version = 0L
    private var accountJob = SupervisorJob()
    private var auth: Deferred<Result<Unit, Error.DataError>>? = null
    private var cleanup: Deferred<Unit>? = null
    private var pendingRegistration: String? = null
    private var monotonicLogin: Long? = null
    private var retryAt: Long? = null
    private var authFailed = false
    private var cleanupPending = false
    private var rejectedAuthentication: Long? = null
    private var cleanupRequest = 0L

    suspend fun restore(): Result<Unit, Error.DataError> {
        try {
            writes.withLock {
                synchronized(guard) { if (restored) return@withLock }
                val owner = currentGeneration()
                val saved = try {
                    preferences.getSession().first()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    throw SessionFailure(NetworkError.Storage)
                }
                synchronized(guard) {
                    checkOwner(owner)
                    record = saved
                    monotonicLogin = saved.loggedInAt?.let { loggedInAt ->
                        (clock.now() - loggedInAt).takeIf { it in 0 until LOGIN_WINDOW }
                            ?.let { age -> clock.elapsed() - age }
                    }
                    restored = true
                    _state.value =
                        snapshot(if (saved.password.isBlank()) SessionPhase.LoginRequired else SessionPhase.Restoring)
                }
            }
            if (synchronized(guard) { record.password.isBlank() }) return Result.Error(NetworkError.LoginRequired)
            ensureSession()
            return Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val error = errorOf(e)
            synchronized(guard) {
                if (_state.value.phase != SessionPhase.LoginRequired) fail(error)
            }
            return Result.Error(error)
        }
    }

    fun currentGeneration(): Long = synchronized(guard) { generation }
    fun isCurrent(owner: Long): Boolean = synchronized(guard) { owner == generation }

    fun publish(owner: Long, block: () -> Unit) = synchronized(guard) {
        checkOwner(owner)
        block()
    }

    /** Cancel account work immediately and also reject sources that ignore cancellation. */
    suspend fun <T> owned(block: suspend (Long) -> T): T = coroutineScope {
        val owner: Long
        val lifetime: Job
        synchronized(guard) { owner = generation; lifetime = accountJob }
        val operation = currentCoroutineContext().job
        val handle = lifetime.invokeOnCompletion { operation.cancel() }
        try {
            val value = block(owner)
            currentCoroutineContext().ensureActive()
            synchronized(guard) { checkOwner(owner) }
            value
        } finally {
            handle.dispose()
        }
    }

    suspend fun login(registration: String, password: String): Result<Unit, Error.DataError> {
        val task = synchronized(guard) {
            auth?.takeIf { it.isActive } ?: run {
                val replacingCredentials = record.password.isNotBlank()
                revokeMemory()
                restored = true
                record = Session(registration = registration)
                if (replacingCredentials) {
                    pendingRegistration = ""
                    cleanupPending = true
                    cleanupRequest++
                    scheduleCleanup()
                }
                startAuthentication(registration, password, manual = true)
            }
        }
        return task.await()
    }

    /** Revocation is synchronous; disk cleanup runs off the UI thread and orders subsequent login. */
    fun logout() {
        synchronized(guard) {
            revokeMemory()
            restored = true
            record = Session()
            pendingRegistration = ""
            cleanupPending = true
            cleanupRequest++
            _state.value = snapshot(SessionPhase.LoginRequired, cleanupPending = true)
            scheduleCleanup()
        }
    }

    suspend fun retry(): Result<Unit, Error.DataError> {
        val pending = synchronized(guard) {
            if (cleanupPending) scheduleCleanup() else null
        }
        if (pending != null) {
            pending.await()
            if (synchronized(guard) { cleanupPending }) return Result.Error(NetworkError.Storage)
            if (state.value.phase == SessionPhase.LoginRequired) return Result.Success(Unit)
        }
        synchronized(guard) {
            if (auth?.isCompleted == true) auth = null
            _state.value = _state.value.copy(error = null)
        }
        return restore()
    }

    suspend fun foreground(): Result<Unit, Error.DataError> {
        val before = synchronized(guard) {
            if (auth?.isCompleted == true) auth = null
            freshness.revision.value
        }
        val result = restore()
        synchronized(guard) {
            // Authentication may already have made every screen stale during this return.
            if (freshness.revision.value == before) {
                cache.clearCache()
                freshness.invalidate()
            }
        }
        return result
    }

    suspend fun <T> protectedRequest(block: suspend (SessionTicket) -> Response<T>): Response<T> =
        owned { owner ->
            ensureSession()
            var ticket = synchronized(guard) {
                checkOwner(owner)
                retryAt?.let {
                    if (clock.now() < it) throw SessionFailure(
                        NetworkError.Http(
                            429,
                            it
                        )
                    )
                }
                ticket()
            }
            var response = block(ticket)
            synchronized(guard) { checkOwner(owner) }
            if (response.code() == 401) {
                response.errorBody()?.close()
                synchronized(guard) {
                    if (ticket.version == version) rejectedAuthentication = version
                }
                ensureSession(rejectedVersion = ticket.version)
                ticket = synchronized(guard) { checkOwner(owner); ticket() }
                response = block(ticket)
                synchronized(guard) {
                    checkOwner(owner)
                    if (response.code() == 401 && ticket.version == version) {
                        response.errorBody()?.close()
                        requireLogin()
                        throw SessionFailure(NetworkError.LoginRequired)
                    }
                }
            }
            mergeCookies(ticket, response)
            if (response.code() == 429 || response.code() == 503) synchronized(guard) {
                checkOwner(owner)
                retryAt = retryTime(response.headers()["Retry-After"])
            }
            response
        }

    private suspend fun ensureSession(rejectedVersion: Long? = null) {
        val task = synchronized(guard) {
            if (record.password.isBlank()) throw SessionFailure(NetworkError.LoginRequired)
            if (auth?.isCompleted == true && !authFailed) auth = null
            auth?.takeIf { it.isActive } ?: if (
                (rejectedVersion != null && rejectedVersion != version && usable()) ||
                (rejectedVersion == null && usable())
            ) {
                _state.value = snapshot(SessionPhase.Authenticated)
                null
            } else {
                auth ?: startAuthentication(record.registration, record.password, manual = false)
            }
        }
        when (val result = task?.await()) {
            is Result.Error -> throw SessionFailure(result.error)
            else -> Unit
        }
    }

    private fun startAuthentication(
        registration: String,
        password: String,
        manual: Boolean
    ): Deferred<Result<Unit, Error.DataError>> {
        val owner = generation
        authFailed = false
        _state.value = snapshot(SessionPhase.Authenticating)
        return scope.async(start = CoroutineStart.LAZY) {
            try {
                cleanup?.await()
                if (synchronized(guard) { cleanupPending }) throw SessionFailure(NetworkError.Storage)
                synchronized(guard) {
                    checkOwner(owner)
                    retryAt?.let {
                        if (clock.now() < it) throw SessionFailure(
                            NetworkError.Http(
                                429,
                                it
                            )
                        )
                    }
                }
                val response = api.login(LoginDTO(registration, password))
                synchronized(guard) { checkOwner(owner) }
                if (!response.isSuccessful) {
                    val body = response.errorBody()?.use { it.string() }
                    val error = if (response.code() == 400 && rejectsPassword(body)) {
                        NetworkError.InvalidCredentials
                    } else NetworkError.Http(
                        response.code(),
                        retryTime(response.headers()["Retry-After"])
                    )
                    synchronized(guard) { retryAt = (error as? NetworkError.Http)?.retryAt }
                    throw SessionFailure(error)
                }
                response.body()?.close()
                val url = response.raw().request.url
                val updates = Cookie.parseAll(url, response.headers())
                if (updates.none { it.expiresAt > clock.now() && it.matches(url) && it.value.isNotBlank() }) throw SessionFailure(
                    NetworkError.Protocol
                )
                val cookies = synchronized(guard) {
                    checkOwner(owner)
                    val merged = jar().associateBy { cookieKey(it) }.toMutableMap()
                    updates.forEach { merged[cookieKey(it)] = it }
                    merged.values.filter { it.expiresAt > clock.now() }
                }
                val saved = Session(
                    cookies = cookies.map { it.toString() }.toSet(), registration = registration,
                    password = password, loggedInAt = clock.now()
                )
                writes.withLock {
                    synchronized(guard) { checkOwner(owner) }
                    persist(saved)
                    synchronized(guard) {
                        checkOwner(owner)
                        record = saved
                        monotonicLogin = clock.elapsed()
                        version++
                        rejectedAuthentication = null
                        retryAt = null
                        _state.value = snapshot(SessionPhase.Authenticated)
                        cache.clearCache()
                        freshness.invalidate()
                    }
                }
                Result.Success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val error = errorOf(e)
                synchronized(guard) {
                    checkOwner(owner)
                    authFailed = true
                    if (!manual && error == NetworkError.InvalidCredentials) requireLogin()
                    else fail(error, manual)
                }
                Result.Error(error)
            }
        }.also { auth = it; it.start() }
    }

    private suspend fun <T> mergeCookies(ticket: SessionTicket, response: Response<T>) {
        if (!response.isSuccessful) return
        val updates = Cookie.parseAll(response.raw().request.url, response.headers())
        if (updates.isEmpty()) return
        writes.withLock {
            val saved = synchronized(guard) {
                checkOwner(ticket.generation)
                if (ticket.version != version) return
                val merged = jar().associateBy { cookieKey(it) }.toMutableMap()
                updates.forEach { merged[cookieKey(it)] = it }
                record.copy(cookies = merged.values.filter { it.expiresAt > clock.now() }
                    .map { it.toString() }.toSet())
            }
            persist(saved)
            synchronized(guard) { checkOwner(ticket.generation); record = saved }
        }
    }

    private fun usable(): Boolean {
        val now = clock.now()
        val login = record.loggedInAt ?: return false
        val elapsed = now - login
        val monotonic = monotonicLogin?.let { clock.elapsed() - it }
        return rejectedAuthentication != version && login > 0 && elapsed in 0 until LOGIN_WINDOW &&
                (monotonic == null || monotonic in 0 until LOGIN_WINDOW) &&
                jar().any { it.expiresAt > now && it.value.isNotBlank() && it.matches(baseUrl.toHttpUrl()) }
    }

    private fun jar(): List<Cookie> =
        record.cookies.mapNotNull { Cookie.parse(baseUrl.toHttpUrl(), it) }

    private fun ticket() = SessionTicket(generation, version, jar())
    private fun cookieKey(cookie: Cookie) = Triple(cookie.name, cookie.domain, cookie.path)
    private fun checkOwner(owner: Long) {
        if (owner != generation) throw CancellationException("Account revoked")
    }

    private fun snapshot(
        phase: SessionPhase,
        error: Error.DataError? = null,
        cleanupPending: Boolean = this.cleanupPending
    ) =
        SessionSnapshot(
            phase,
            generation,
            record.registration,
            error,
            record.password.isNotBlank() && record.loggedInAt != null,
            cleanupPending
        )

    private fun fail(error: Error.DataError, manual: Boolean = false) {
        _state.value = snapshot(
            if (manual) SessionPhase.LoginRequired else SessionPhase.RecoverableFailure, error,
            cleanupPending = cleanupPending
        )
    }

    private fun revokeMemory() {
        generation++
        accountJob.cancel()
        accountJob = SupervisorJob()
        auth?.cancel()
        auth = null
        monotonicLogin = null
        cache.clearCache()
    }

    private fun requireLogin() {
        val registration = record.registration
        // Do not cancel the authentication task that is publishing this terminal outcome.
        auth = null
        revokeMemory()
        record = Session(registration = registration)
        cleanupPending = true
        cleanupRequest++
        pendingRegistration = registration
        _state.value =
            snapshot(SessionPhase.LoginRequired, NetworkError.LoginRequired, cleanupPending = true)
        scheduleCleanup()
    }

    private fun scheduleCleanup(): Deferred<Unit> {
        cleanup?.takeIf { it.isActive }?.let { return it }
        return scope.async(start = CoroutineStart.LAZY) {
            try {
                while (true) {
                    val (request, registration) = synchronized(guard) {
                        cleanupRequest to pendingRegistration.orEmpty()
                    }
                    writes.withLock {
                        if (registration.isBlank()) preferences.clearSession()
                        else preferences.replaceSession(Session(registration = registration))
                    }
                    val complete = synchronized(guard) {
                        if (request != cleanupRequest) false else {
                            cleanupPending = false
                            _state.value = _state.value.copy(
                                cleanupPending = false,
                                error = _state.value.error?.takeUnless { it == NetworkError.Storage })
                            true
                        }
                    }
                    if (complete) break
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                synchronized(guard) {
                    _state.value =
                        _state.value.copy(error = NetworkError.Storage, cleanupPending = true)
                }
            }
        }.also { cleanup = it; it.start() }
    }

    private suspend fun persist(saved: Session) {
        try {
            preferences.replaceSession(saved)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw SessionFailure(NetworkError.Storage)
        }
    }

    private fun retryTime(value: String?): Long? = value?.let {
        it.toLongOrNull()?.let { seconds -> clock.now() + seconds.coerceIn(0, 86400) * 1000 }
            ?: runCatching {
                ZonedDateTime.parse(it, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant()
                    .toEpochMilli()
            }.getOrNull()
    }

    companion object {
        const val LOGIN_WINDOW = 15 * 60 * 1000L
        fun rejectsPassword(body: String?): Boolean = runCatching {
            val root = JsonParser.parseString(body ?: return false)
            root.isJsonObject && root.asJsonObject.get("")?.let { messages ->
                messages.isJsonArray && messages.asJsonArray.any {
                    it.isJsonPrimitive && it.asJsonPrimitive.isString && it.asString == "Senha Inválida!"
                }
            } == true
        }.getOrDefault(false)

        fun errorOf(e: Exception): Error.DataError = when (e) {
            is CancellationException -> throw e
            is SessionFailure -> e.reason
            is com.google.gson.stream.MalformedJsonException -> NetworkError.Protocol
            is java.io.EOFException -> NetworkError.Protocol
            is IOException -> IOError
            else -> NetworkError.Protocol
        }
    }
}
