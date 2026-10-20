package com.adielcalixto.ifacademico.data.session

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.remote.dto.PeriodDTO
import com.adielcalixto.ifacademico.data.usecases.AcademicLoad
import com.adielcalixto.ifacademico.domain.NetworkError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.Session
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class SessionCoordinatorTest {
    private val api = mockk<AcademicoAPI>()
    private val clock = FakeClock()

    @Test
    fun `recent restoration and protected requests never validate or extend the login clock`() =
        runTest {
            for (age in listOf(0L, SessionCoordinator.LOGIN_WINDOW - 1)) {
                val preferences = MemoryPreferences(savedSession(clock, age))
                val session = coordinator(preferences, api, clock, backgroundScope)
                assertTrue(session.restore() is Result.Success)
                session.protectedRequest { ticket ->
                    assertTrue(ticket.cookies.any { it.name == "session" && it.value == "old" })
                    httpResponse(emptyList<PeriodDTO>(), cookies = listOf("route=updated; Path=/"))
                }
                assertEquals(clock.now() - age, preferences.saved.loggedInAt)
                assertEquals(2, preferences.saved.cookies.size)
            }
            coVerify(exactly = 0) { api.login(any()) }
            coVerify(exactly = 0) { api.getStudent(any()) }
        }

    @Test
    fun `exact expiry old sessions missing cookies timestamps and backward clocks authenticate before work`() =
        runTest {
            coEvery { api.login(any()) } answers { loginResponse() }
            val invalid = listOf(
                savedSession(clock, SessionCoordinator.LOGIN_WINDOW),
                savedSession(clock, SessionCoordinator.LOGIN_WINDOW + 1),
                savedSession(clock).copy(cookies = emptySet()),
                savedSession(clock).copy(loggedInAt = null),
                savedSession(clock).copy(loggedInAt = clock.now() + 1),
                savedSession(clock).copy(loggedInAt = -1),
            )
            for (saved in invalid) {
                val preferences = MemoryPreferences(saved)
                val session = coordinator(preferences, api, clock, backgroundScope)
                session.restore()
                session.protectedRequest { ticket ->
                    assertEquals("new", ticket.cookies.first { it.name == "session" }.value)
                    httpResponse(Unit)
                }
                assertEquals(clock.now(), preferences.saved.loggedInAt)
            }
            coVerify(exactly = invalid.size) { api.login(any()) }
        }

    @Test
    fun `a successful login expires again and monotonic elapsed time bounds live clock changes`() =
        runTest {
            coEvery { api.login(any()) } answers { loginResponse() }
            val session = coordinator(
                MemoryPreferences(savedSession(clock, SessionCoordinator.LOGIN_WINDOW)),
                api,
                clock,
                backgroundScope
            )
            session.restore()
            clock.ticks += SessionCoordinator.LOGIN_WINDOW
            session.protectedRequest { httpResponse(Unit) }
            coVerify(exactly = 2) { api.login(any()) }
            clock.advance(SessionCoordinator.LOGIN_WINDOW)
            session.protectedRequest { httpResponse(Unit) }
            coVerify(exactly = 3) { api.login(any()) }
        }

    @Test
    fun `concurrent failures and a delayed old 401 share successful authentication`() = runTest {
        val preferences = MemoryPreferences(savedSession(clock))
        val session = coordinator(preferences, api, clock, backgroundScope)
        session.restore()
        val releaseLogin = CompletableDeferred<Unit>()
        val releaseLate = CompletableDeferred<Unit>()
        val bothStarted = CompletableDeferred<Unit>()
        coEvery { api.login(any()) } coAnswers { releaseLogin.await(); loginResponse() }
        var calls = 0
        val first = async {
            session.protectedRequest { ticket ->
                calls++
                if (ticket.version == 0L) {
                    bothStarted.await(); Response.error(401, "{}".toResponseBody())
                } else httpResponse(Unit)
            }
        }
        val late = async {
            session.protectedRequest { ticket ->
                calls++
                if (ticket.version == 0L) {
                    bothStarted.complete(Unit); releaseLate.await(); Response.error(
                        401,
                        "{}".toResponseBody()
                    )
                } else httpResponse(Unit)
            }
        }
        runCurrent()
        releaseLogin.complete(Unit)
        runCurrent()
        first.await()
        releaseLate.complete(Unit)
        late.await()
        assertEquals(4, calls)
        coVerify(exactly = 1) { api.login(any()) }
        assertEquals(SessionPhase.Authenticated, session.state.value.phase)
    }

    @Test
    fun `retry 401 revokes credentials retains registration and bounds recovery`() = runTest {
        coEvery { api.login(any()) } answers { loginResponse() }
        val preferences = MemoryPreferences(savedSession(clock))
        val cache = MemoryCache()
        val session = coordinator(preferences, api, clock, backgroundScope, cache)
        session.restore()
        var requests = 0
        try {
            session.protectedRequest<Unit> {
                requests++; Response.error(
                401,
                "{}".toResponseBody()
            )
            }
        } catch (_: CancellationException) {
        } catch (_: SessionFailure) {
        }
        runCurrent()
        assertEquals(2, requests)
        assertEquals(SessionPhase.LoginRequired, session.state.value.phase)
        assertEquals("synthetic-student", preferences.saved.registration)
        assertTrue(preferences.saved.password.isEmpty())
        assertTrue(preferences.saved.cookies.isEmpty())
        assertTrue(cache.entries.isEmpty())
        coVerify(exactly = 1) { api.login(any()) }
    }

    @Test
    fun `only actual password rejection clears automatic credentials`() = runTest {
        for (body in listOf(
            "",
            "<html>outage</html>",
            "null",
            "[]",
            "{}",
            "{\"\":null}",
            "{\"\":[1]}",
            "{\"\":[\"other\"]}"
        )) {
            coEvery { api.login(any()) } returns Response.error(400, body.toResponseBody())
            val preferences =
                MemoryPreferences(savedSession(clock, SessionCoordinator.LOGIN_WINDOW))
            val session = coordinator(preferences, api, clock, backgroundScope)
            assertTrue(session.restore() is Result.Error)
            assertEquals(SessionPhase.RecoverableFailure, session.state.value.phase)
            assertEquals("synthetic-password", preferences.saved.password)
        }
        coEvery { api.login(any()) } returns Response.error(
            400,
            """{"": ["Senha Inválida!"]}""".toResponseBody()
        )
        val preferences = MemoryPreferences(savedSession(clock, SessionCoordinator.LOGIN_WINDOW))
        val session = coordinator(preferences, api, clock, backgroundScope)
        session.restore()
        runCurrent()
        assertEquals(SessionPhase.LoginRequired, session.state.value.phase)
        assertEquals(NetworkError.LoginRequired, session.state.value.error)
        assertEquals("synthetic-student", preferences.saved.registration)
        assertEquals("", preferences.saved.password)
    }

    @Test
    fun `manual password rejection stays on form and cookie-less success is recoverable`() =
        runTest {
            val preferences = MemoryPreferences()
            val session = coordinator(preferences, api, clock, backgroundScope)
            coEvery { api.login(any()) } returns Response.error(
                400,
                """{"": ["Senha Inválida!"]}""".toResponseBody()
            )
            assertEquals(
                Result.Error<Unit, NetworkError>(NetworkError.InvalidCredentials),
                session.login("student", "wrong")
            )
            assertEquals(SessionPhase.LoginRequired, session.state.value.phase)
            assertEquals("", preferences.saved.password)
            coEvery { api.login(any()) } answers { httpResponse("{}".toResponseBody()) }
            assertEquals(
                Result.Error<Unit, NetworkError>(NetworkError.Protocol),
                session.login("student", "correct")
            )
            assertNull(preferences.saved.loggedInAt)
        }

    @Test
    fun `transient authentication failure is shared until explicit retry and then clears errors`() =
        runTest {
            val release = CompletableDeferred<Unit>()
            coEvery { api.login(any()) } coAnswers { release.await(); throw java.io.IOException("offline") }
            val preferences =
                MemoryPreferences(savedSession(clock, SessionCoordinator.LOGIN_WINDOW))
            val session = coordinator(preferences, api, clock, backgroundScope)
            val restore = async { session.restore() }
            runCurrent()
            val request = async {
                runCatching { session.protectedRequest { httpResponse(Unit) } }
            }
            release.complete(Unit)
            restore.await()
            request.await()
            coVerify(exactly = 1) { api.login(any()) }
            assertEquals("synthetic-password", preferences.saved.password)
            coEvery { api.login(any()) } answers { loginResponse() }
            assertTrue(session.retry() is Result.Success)
            assertNull(session.state.value.error)
            assertEquals(SessionPhase.Authenticated, session.state.value.phase)
        }

    @Test
    fun `logout wins over a non cancellable login response and remains durable on restart`() =
        runTest {
            val release = CompletableDeferred<Unit>()
            coEvery { api.login(any()) } coAnswers { withContext(NonCancellable) { release.await() }; loginResponse() }
            val preferences = MemoryPreferences()
            val session = coordinator(preferences, api, clock, backgroundScope)
            val login = async { session.login("student", "password") }
            runCurrent()
            session.logout()
            release.complete(Unit)
            runCurrent()
            assertTrue(login.isCancelled)
            assertEquals("", preferences.saved.password)
            assertEquals(SessionPhase.LoginRequired, session.state.value.phase)
            assertFalse(session.state.value.cleanupPending)
            val restarted = coordinator(preferences, api, clock, backgroundScope)
            restarted.restore()
            assertEquals(SessionPhase.LoginRequired, restarted.state.value.phase)
        }

    @Test
    fun `logout invalidates late academic responses and cannot populate a replacement account cache`() =
        runTest {
            val release = CompletableDeferred<Unit>()
            val preferences = MemoryPreferences(savedSession(clock))
            val cache = MemoryCache()
            val session = coordinator(preferences, api, clock, backgroundScope, cache)
            session.restore()
            val load = AcademicLoad(cache, session)
            val old = async {
                load.cached("academic", {
                    withContext(NonCancellable) { release.await() }
                    httpResponse(listOf("private old account data"))
                }) { it }
            }
            runCurrent()
            session.logout()
            coEvery { api.login(any()) } answers { loginResponse() }
            assertTrue(session.login("another-student", "password") is Result.Success)
            release.complete(Unit)
            runCurrent()
            assertTrue(old.isCancelled)
            assertTrue(cache.entries.isEmpty())
            assertEquals("another-student", preferences.saved.registration)
        }

    @Test
    fun `failed logout cleanup is visible retryable and orders the next login`() = runTest {
        val preferences = MemoryPreferences(savedSession(clock))
        val session = coordinator(preferences, api, clock, backgroundScope)
        session.restore()
        preferences.failWrite = true
        session.logout()
        runCurrent()
        assertEquals(SessionPhase.LoginRequired, session.state.value.phase)
        assertEquals(NetworkError.Storage, session.state.value.error)
        assertTrue(session.state.value.cleanupPending)
        assertTrue(session.login("another-student", "password") is Result.Error)
        coVerify(exactly = 0) { api.login(any()) }
        preferences.failWrite = false
        session.retry()
        assertFalse(session.state.value.cleanupPending)
        assertTrue(preferences.saved.password.isEmpty())
    }

    @Test
    fun `storage read failure preserves credentials and retry restores instead of declaring password rejection`() =
        runTest {
            val preferences = MemoryPreferences(savedSession(clock)).apply { failRead = true }
            val session = coordinator(preferences, api, clock, backgroundScope)
            session.restore()
            assertEquals(NetworkError.Storage, session.state.value.error)
            assertEquals("synthetic-password", preferences.saved.password)
            preferences.failRead = false
            assertTrue(session.retry() is Result.Success)
            assertEquals(SessionPhase.Authenticated, session.state.value.phase)
        }

    @Test
    fun `cookie merging honors deletion scope and retains login time`() = runTest {
        val preferences = MemoryPreferences(savedSession(clock))
        val session = coordinator(preferences, api, clock, backgroundScope)
        session.restore()
        val loginTime = preferences.saved.loggedInAt
        session.protectedRequest {
            httpResponse(
                Unit,
                cookies = listOf("route=gone; Max-Age=0; Path=/", "detail=only; Path=/details")
            )
        }
        assertEquals(loginTime, preferences.saved.loggedInAt)
        session.protectedRequest { ticket ->
            assertTrue(ticket.cookies.any { it.name == "session" })
            assertFalse(ticket.cookies.any { it.name == "route" })
            assertFalse(ticket.cookies.first { it.name == "detail" }.matches(TEST_URL.toHttpUrl()))
            httpResponse(Unit)
        }
    }

    @Test
    fun `restored session also uses monotonic expiry when the wall clock moves backward within its window`() =
        runTest {
            coEvery { api.login(any()) } answers { loginResponse() }
            val session = coordinator(
                MemoryPreferences(savedSession(clock, 5 * 60 * 1000)),
                api,
                clock,
                backgroundScope
            )
            session.restore()
            clock.ticks += 10 * 60 * 1000
            clock.time += 60 * 1000
            session.protectedRequest { httpResponse(Unit) }
            coVerify(exactly = 1) { api.login(any()) }
        }

    @Test
    fun `temporary login statuses retain credentials and respect Retry After without automatic loops`() =
        runTest {
            for (status in listOf(400, 429, 500, 503)) {
                coEvery { api.login(any()) } returns Response.error(status, "{}".toResponseBody())
                val preferences =
                    MemoryPreferences(savedSession(clock, SessionCoordinator.LOGIN_WINDOW))
                val session = coordinator(preferences, api, clock, backgroundScope)
                assertEquals(
                    Result.Error<Unit, NetworkError>(NetworkError.Http(status)),
                    session.restore()
                )
                assertEquals("synthetic-password", preferences.saved.password)
            }
        }

    @Test
    fun `logout while a non cancellable cookie write is suspended serializes cleanup after it`() =
        runTest {
            val preferences = MemoryPreferences(savedSession(clock))
            val session = coordinator(preferences, api, clock, backgroundScope)
            session.restore()
            val release = CompletableDeferred<Unit>()
            var suspendWrite = true
            preferences.beforeWrite = {
                if (suspendWrite) {
                    suspendWrite = false
                    withContext(NonCancellable) { release.await() }
                }
            }
            val request = async {
                session.protectedRequest {
                    httpResponse(
                        Unit,
                        cookies = listOf("session=obsolete; Path=/")
                    )
                }
            }
            runCurrent()
            session.logout()
            assertEquals(SessionPhase.LoginRequired, session.state.value.phase)
            release.complete(Unit)
            runCurrent()
            assertTrue(request.isCancelled)
            assertTrue(preferences.saved.cookies.isEmpty())
            assertTrue(preferences.saved.password.isEmpty())
            assertFalse(session.state.value.cleanupPending)
        }

    @Test
    fun `logout after terminal rejection while cleanup is suspended also forgets registration`() =
        runTest {
            val preferences =
                MemoryPreferences(savedSession(clock, SessionCoordinator.LOGIN_WINDOW))
            val session = coordinator(preferences, api, clock, backgroundScope)
            val release = CompletableDeferred<Unit>()
            var suspendWrite = true
            preferences.beforeWrite = {
                if (suspendWrite) {
                    suspendWrite = false
                    withContext(NonCancellable) { release.await() }
                }
            }
            coEvery { api.login(any()) } returns Response.error(
                400,
                """{"": ["Senha Inválida!"]}""".toResponseBody()
            )
            session.restore()
            runCurrent()
            session.logout()
            release.complete(Unit)
            runCurrent()
            assertTrue(preferences.saved.registration.isEmpty())
            assertTrue(preferences.saved.password.isEmpty())
        }

    @Test
    fun `foreground authentication and freshness invalidation coalesce and recent returns still invalidate`() =
        runTest {
            val session = coordinator(
                MemoryPreferences(savedSession(clock, SessionCoordinator.LOGIN_WINDOW)),
                api,
                clock,
                backgroundScope
            )
            coEvery { api.login(any()) } answers { loginResponse() }
            session.foreground()
            assertEquals(1L, session.freshness.revision.value)
            session.foreground()
            assertEquals(2L, session.freshness.revision.value)
            coVerify(exactly = 1) { api.login(any()) }
        }

    @Test
    fun `failed replacement login cannot restore the previous account on restart`() = runTest {
        val preferences = MemoryPreferences(savedSession(clock))
        val session = coordinator(preferences, api, clock, backgroundScope)
        session.restore()
        coEvery { api.login(any()) } throws java.io.IOException("offline")
        session.login("different-student", "password")
        assertTrue(preferences.saved.registration.isEmpty())
        assertTrue(preferences.saved.password.isEmpty())
        val restarted = coordinator(preferences, api, clock, backgroundScope)
        restarted.restore()
        assertEquals(SessionPhase.LoginRequired, restarted.state.value.phase)
    }
}
