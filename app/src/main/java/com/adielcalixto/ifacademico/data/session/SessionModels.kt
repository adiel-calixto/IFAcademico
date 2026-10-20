package com.adielcalixto.ifacademico.data.session

import com.adielcalixto.ifacademico.domain.Error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.Cookie
import javax.inject.Inject
import javax.inject.Singleton

/** Both clocks are injectable: wall time survives process death; monotonic time bounds a live session. */
interface SessionClock {
    fun now(): Long
    fun elapsed(): Long
}

@Singleton
class SystemSessionClock @Inject constructor() : SessionClock {
    override fun now() = System.currentTimeMillis()
    override fun elapsed() = android.os.SystemClock.elapsedRealtime()
}

enum class SessionPhase { Restoring, Authenticating, Authenticated, RecoverableFailure, LoginRequired }

data class SessionSnapshot(
    val phase: SessionPhase = SessionPhase.Restoring,
    val generation: Long = 0,
    val registration: String = "",
    val error: Error.DataError? = null,
    val hasSession: Boolean = false,
    val cleanupPending: Boolean = false,
)

data class SessionTicket(val generation: Long, val version: Long, val cookies: List<Cookie>)

class SessionFailure(val reason: Error.DataError) : Exception()

@Singleton
class AcademicFreshness @Inject constructor() {
    private val _revision = MutableStateFlow(0L)
    val revision = _revision.asStateFlow()
    fun invalidate() {
        _revision.update { it + 1 }
    }
}
