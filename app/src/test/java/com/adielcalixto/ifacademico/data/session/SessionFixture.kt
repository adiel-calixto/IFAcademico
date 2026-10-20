package com.adielcalixto.ifacademico.data.session

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.local.SessionPreferences
import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.domain.entities.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

internal class FakeClock(var time: Long = System.currentTimeMillis(), var ticks: Long = 1000) :
    SessionClock {
    override fun now() = time
    override fun elapsed() = ticks
    fun advance(millis: Long) {
        time += millis; ticks += millis
    }
}

internal class MemoryPreferences(var saved: Session = Session()) : SessionPreferences {
    var writes = 0
    var failWrite = false
    var failRead = false
    var beforeWrite: suspend () -> Unit = {}
    override fun getSession() = flow {
        if (failRead) throw java.io.IOException("storage unavailable")
        emit(saved)
    }

    override suspend fun replaceSession(session: Session) {
        beforeWrite()
        if (failWrite) throw java.io.IOException("disk unavailable")
        saved = session
        writes++
    }

    override suspend fun clearSession() = replaceSession(Session())
}

internal class MemoryCache : CacheService {
    val entries = mutableMapOf<String, String>()
    override fun get(key: String) = entries[key].orEmpty()
    override fun put(key: String, data: String) {
        entries[key] = data
    }

    override fun clearCache() {
        entries.clear()
    }
}

internal const val TEST_URL = "https://example.test/webapp/api/"
internal fun savedSession(clock: FakeClock, age: Long = 0) = Session(
    cookies = setOf("session=old; Path=/; Secure; HttpOnly", "route=keep; Path=/"),
    registration = "synthetic-student",
    password = "synthetic-password",
    loggedInAt = clock.now() - age,
)

internal fun <T> httpResponse(
    body: T,
    url: String = TEST_URL,
    cookies: List<String> = emptyList()
): Response<T> {
    val headers = Headers.Builder().apply { cookies.forEach { add("Set-Cookie", it) } }.build()
    return Response.success(
        body, okhttp3.Response.Builder().request(Request.Builder().url(url).build())
            .protocol(Protocol.HTTP_1_1).code(200).message("OK").headers(headers).build()
    )
}

internal fun loginResponse(url: String = TEST_URL): Response<ResponseBody> =
    httpResponse("{}".toResponseBody(), url, listOf("session=new; Path=/; HttpOnly"))

internal fun coordinator(
    preferences: MemoryPreferences, api: AcademicoAPI, clock: FakeClock, scope: CoroutineScope,
    cache: MemoryCache = MemoryCache(), url: String = TEST_URL,
) = SessionCoordinator(preferences, api, cache, clock, AcademicFreshness(), url, scope)
