package com.adielcalixto.ifacademico.data.remote

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.remote.interceptors.AddCookiesInterceptor
import com.adielcalixto.ifacademico.data.remote.interceptors.CookieInterceptor
import com.adielcalixto.ifacademico.data.remote.interceptors.UnauthorizedErrorInterceptor
import com.adielcalixto.ifacademico.domain.entities.Session
import com.adielcalixto.ifacademico.domain.usecases.GetSessionUseCase
import com.adielcalixto.ifacademico.domain.usecases.SaveSessionUseCase
import com.adielcalixto.ifacademico.observers.UnauthorizedApiErrorObserver
import com.adielcalixto.ifacademico.observers.UnauthorizedApiErrorSubscriber
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HttpCompatibilityTest {
    private val server = MockWebServer()
    private val getSession = mockk<GetSessionUseCase>()
    private val saveSession = mockk<SaveSessionUseCase>(relaxed = true)
    private val cache = mockk<CacheService>(relaxed = true)
    private val observer = UnauthorizedApiErrorObserver()
    private var unauthorizedCount = 0
    private lateinit var client: OkHttpClient
    private lateinit var api: AcademicoAPI

    @Before
    fun setUp() {
        server.start()
        coEvery { getSession.execute() } returns Session(
            cookies = setOf("session=fake", "route=test"),
            registration = "test-student",
            password = "synthetic-password"
        )
        observer.subscribe(object : UnauthorizedApiErrorSubscriber {
            override fun onUnauthorizedError() {
                unauthorizedCount++
            }
        })
        // Keep the same interception order as the production academic client.
        client = OkHttpClient.Builder()
            .addInterceptor(UnauthorizedErrorInterceptor(observer, cache))
            .addInterceptor(CookieInterceptor(saveSession))
            .addInterceptor(AddCookiesInterceptor(getSession))
            .build()
        api = Retrofit.Builder()
            .baseUrl(server.url("/webapp/api/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AcademicoAPI::class.java)
    }

    @After
    fun tearDown() {
        server.close()
        client.connectionPool.evictAll()
        client.dispatcher.executorService.shutdown()
    }

    @Test
    fun `Retrofit decodes academic payload and interceptors exchange cookies`() = runTest {
        server.enqueue(
            MockResponse.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Set-Cookie", "session=renewed; Path=/; HttpOnly")
                .addHeader("Set-Cookie", "route=next; Path=/")
                .body("""[{"anoLetivo":2026,"periodoLetivo":2}]""")
                .build()
        )

        val response = api.getPeriods()
        assertEquals(200, response.code())
        val period = response.body()!!.single().toDomain()
        assertEquals(2026, period.year)
        assertEquals(2, period.number)
        val request = server.takeRequest(5, TimeUnit.SECONDS)
        assertNotNull(request)
        assertEquals("/webapp/api/diarios/aluno/periodos", request!!.url.encodedPath)
        assertEquals("session=fake; route=test", request.headers["Cookie"])
        coVerify(exactly = 1) {
            saveSession.execute(match { it.cookies == setOf("session=renewed", "route=next") })
        }
        assertEquals(0, unauthorizedCount)
        verify(exactly = 0) { cache.clearCache() }
    }

    @Test
    fun `HTTP 401 notifies session handling and clears cached academic data`() = runTest {
        server.enqueue(MockResponse.Builder().code(401).body("{}").build())

        val response = api.getPeriods()

        assertFalse(response.isSuccessful)
        assertEquals(401, response.code())
        response.errorBody()?.close()
        assertEquals(1, unauthorizedCount)
        verify(exactly = 1) { cache.clearCache() }
        coVerify(exactly = 0) { saveSession.execute(any()) }
    }
}
