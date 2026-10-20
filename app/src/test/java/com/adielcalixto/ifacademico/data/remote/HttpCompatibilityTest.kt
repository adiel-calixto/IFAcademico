package com.adielcalixto.ifacademico.data.remote

import com.adielcalixto.ifacademico.data.session.*
import com.adielcalixto.ifacademico.domain.NetworkError
import com.adielcalixto.ifacademico.domain.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HttpCompatibilityTest {
    private val server = MockWebServer()
    private val client = academicHttpClient()
    private val clock = FakeClock()
    private lateinit var raw: AcademicoAPI
    private lateinit var preferences: MemoryPreferences

    @Before
    fun setup() {
        server.start()
        raw = Retrofit.Builder().baseUrl(server.url("/webapp/api/"))
            .client(client).addConverterFactory(GsonConverterFactory.create())
            .build().create(AcademicoAPI::class.java)
        preferences = MemoryPreferences(
            savedSession(clock).copy(
                cookies = setOf("session=old; Path=/; HttpOnly", "route=keep; Path=/"),
            )
        )
    }

    @After
    fun teardown() {
        server.close()
        client.connectionPool.evictAll()
        client.dispatcher.executorService.shutdown()
    }

    private fun login() =
        MockResponse.Builder().body("{}").addHeader("Set-Cookie", "session=new; Path=/; HttpOnly")
            .build()

    private fun periods() =
        MockResponse.Builder().body("""[{"anoLetivo":2026,"periodoLetivo":2}]""").build()

    @Test
    fun `production client recovers an academic 401 and retries exactly once with renewed scoped cookies`() =
        runTest {
            val session = coordinator(
                preferences,
                raw,
                clock,
                backgroundScope,
                url = server.url("/webapp/api/").toString()
            )
            val api = SessionAcademicoAPI(raw, session)
            session.restore()
            server.enqueue(MockResponse.Builder().code(401).body("{}").build())
            server.enqueue(login())
            server.enqueue(periods())

            assertEquals(2026, api.getPeriods().body()!!.single().toDomain().year)
            assertEquals(3, server.requestCount)
            val original = server.takeRequest(5, TimeUnit.SECONDS)!!
            val auth = server.takeRequest(5, TimeUnit.SECONDS)!!
            val retry = server.takeRequest(5, TimeUnit.SECONDS)!!
            assertEquals("session=old; route=keep", original.headers["Cookie"])
            assertEquals("/webapp/api/autenticacao/signin", auth.url.encodedPath)
            assertNull(auth.headers["Cookie"])
            assertEquals("session=new; route=keep", retry.headers["Cookie"])
            assertEquals(SessionPhase.Authenticated, session.state.value.phase)
        }

    @Test
    fun `production client merges cookies without replacing unrelated cookies or moving the login clock`() =
        runTest {
            val session = coordinator(
                preferences,
                raw,
                clock,
                backgroundScope,
                url = server.url("/webapp/api/").toString()
            )
            val api = SessionAcademicoAPI(raw, session)
            session.restore()
            val loggedInAt = preferences.saved.loggedInAt
            server.enqueue(
                MockResponse.Builder().body("[]").addHeader("Set-Cookie", "route=next; Path=/")
                    .build()
            )
            server.enqueue(periods())
            api.getPeriods()
            api.getPeriods()
            server.takeRequest()
            val second = server.takeRequest()
            assertEquals("session=old; route=next", second.headers["Cookie"])
            assertEquals(loggedInAt, preferences.saved.loggedInAt)
            assertEquals(2, server.requestCount)
        }

    @Test
    fun `login HTTP failures bypass protected recovery and preserve credentials`() = runTest {
        preferences.saved = preferences.saved.copy(loggedInAt = null)
        val session = coordinator(
            preferences,
            raw,
            clock,
            backgroundScope,
            url = server.url("/webapp/api/").toString()
        )
        server.enqueue(MockResponse.Builder().code(401).body("{}").build())
        assertTrue(session.restore() is Result.Error)
        assertEquals(SessionPhase.RecoverableFailure, session.state.value.phase)
        assertEquals("synthetic-password", preferences.saved.password)
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `bounded client timeouts include a whole call deadline`() {
        assertEquals(15000, client.connectTimeoutMillis)
        assertEquals(20000, client.readTimeoutMillis)
        assertEquals(20000, client.writeTimeoutMillis)
        assertEquals(45000, client.callTimeoutMillis)
        assertFalse(client.retryOnConnectionFailure)
    }

    @Test
    fun `malformed protected payload finishes with recoverable protocol failure`() = runTest {
        val session = coordinator(
            preferences,
            raw,
            clock,
            backgroundScope,
            url = server.url("/webapp/api/").toString()
        )
        session.restore()
        server.enqueue(MockResponse.Builder().body("<html>outage</html>").build())
        val load = com.adielcalixto.ifacademico.data.usecases.AcademicLoad(MemoryCache(), session)
        val result =
            load.cached("periods", { SessionAcademicoAPI(raw, session).getPeriods() }) { it.size }
        assertEquals(Result.Error<Int, NetworkError>(NetworkError.Protocol), result)
        assertEquals(SessionPhase.Authenticated, session.state.value.phase)
    }

    @Test
    fun `concurrent HTTP 401 responses share one login on the production Retrofit path`() =
        runTest {
            val bothOldRequests = java.util.concurrent.CountDownLatch(2)
            server.dispatcher = object : mockwebserver3.Dispatcher() {
                override fun dispatch(request: mockwebserver3.RecordedRequest): MockResponse {
                    if (request.url.encodedPath.endsWith("signin")) return login()
                    return if (request.headers["Cookie"]?.contains("session=old") == true) {
                        bothOldRequests.countDown()
                        check(bothOldRequests.await(5, TimeUnit.SECONDS))
                        MockResponse.Builder().code(401).body("{}").build()
                    } else periods()
                }
            }
            val session = coordinator(
                preferences,
                raw,
                clock,
                backgroundScope,
                url = server.url("/webapp/api/").toString()
            )
            session.restore()
            val api = SessionAcademicoAPI(raw, session)
            val first = async { api.getPeriods() }
            val second = async { api.getPeriods() }
            assertEquals(200, first.await().code())
            assertEquals(200, second.await().code())
            assertEquals(5, server.requestCount)
            val requests = List(5) { server.takeRequest(5, TimeUnit.SECONDS)!! }
            assertEquals(1, requests.count { it.url.encodedPath.endsWith("signin") })
        }

    @Test
    fun `Retry After prevents immediate authentication loops and allows a later explicit retry`() =
        runTest {
            preferences.saved = preferences.saved.copy(loggedInAt = null)
            val session = coordinator(
                preferences,
                raw,
                clock,
                backgroundScope,
                url = server.url("/webapp/api/").toString()
            )
            server.enqueue(
                MockResponse.Builder().code(429).addHeader("Retry-After", "60").body("{}").build()
            )
            assertTrue(session.restore() is Result.Error)
            assertTrue(session.retry() is Result.Error)
            assertEquals(1, server.requestCount)
            assertEquals("synthetic-password", preferences.saved.password)
            clock.advance(60000)
            server.enqueue(login())
            assertTrue(session.retry() is Result.Success)
            assertEquals(2, server.requestCount)
        }

    @Test
    fun `slow server reaches a bounded network timeout and permits recovery`() = runTest {
        val shortClient = client.newBuilder().callTimeout(100, TimeUnit.MILLISECONDS).build()
        val shortRaw = Retrofit.Builder().baseUrl(server.url("/webapp/api/"))
            .client(shortClient).addConverterFactory(GsonConverterFactory.create()).build()
            .create(AcademicoAPI::class.java)
        preferences.saved = preferences.saved.copy(loggedInAt = null)
        val session = coordinator(
            preferences,
            shortRaw,
            clock,
            backgroundScope,
            url = server.url("/webapp/api/").toString()
        )
        server.enqueue(
            MockResponse.Builder().headersDelay(500, TimeUnit.MILLISECONDS).body("{}").build()
        )
        assertTrue(session.restore() is Result.Error)
        assertEquals(SessionPhase.RecoverableFailure, session.state.value.phase)
        assertEquals("synthetic-password", preferences.saved.password)
        server.enqueue(login())
        assertTrue(session.retry() is Result.Success)
    }
}
