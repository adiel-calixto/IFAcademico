package com.adielcalixto.ifacademico.presentation

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.session.*
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.Student
import com.adielcalixto.ifacademico.domain.usecases.GetStudentUseCase
import com.adielcalixto.ifacademico.domain.usecases.SessionState
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val api = mockk<AcademicoAPI>()
    private val student = mockk<GetStudentUseCase>()
    private val clock = FakeClock()
    private val identity = Student("Synthetic student", 42, 24, "Computing")

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        coEvery { student.execute() } returns Result.Success(identity)
        coEvery { api.login(any()) } answers { loginResponse() }
    }

    @After
    fun teardown() = Dispatchers.resetMain()

    @Test
    fun `startup waits for identity before publishing signed in readiness and recent session never logs in`() =
        runTest(dispatcher) {
            val release = CompletableDeferred<Unit>()
            coEvery { student.execute() } coAnswers { release.await(); Result.Success(identity) }
            val session =
                coordinator(MemoryPreferences(savedSession(clock)), api, clock, backgroundScope)
            val model = MainViewModel(session, student)
            runCurrent()
            assertNull(model.state.value.sessionState)
            assertNull(model.state.value.student)
            assertEquals(SessionPhase.Authenticated, session.state.value.phase)
            release.complete(Unit)
            runCurrent()
            assertEquals(SessionState.Valid, model.state.value.sessionState)
            assertEquals(identity, model.state.value.student)
            coVerify(exactly = 0) { api.login(any()) }
            coVerify(exactly = 1) { student.execute() }
        }

    @Test
    fun `temporary identity failure keeps successful authentication and retry clears the error`() =
        runTest(dispatcher) {
            val preferences =
                MemoryPreferences(savedSession(clock, SessionCoordinator.LOGIN_WINDOW))
            val session = coordinator(preferences, api, clock, backgroundScope)
            coEvery { student.execute() } returns Result.Error(IOError)
            val model = MainViewModel(session, student)
            runCurrent()
            assertEquals(IOError, model.state.value.error)
            assertNull(model.state.value.sessionState)
            assertTrue(model.state.value.isResolvingIdentity)
            assertEquals(SessionPhase.Authenticated, session.state.value.phase)
            assertEquals("synthetic-password", preferences.saved.password)
            coEvery { student.execute() } returns Result.Success(identity)
            model.retryLoadingData()
            runCurrent()
            assertNull(model.state.value.error)
            assertEquals(SessionState.Valid, model.state.value.sessionState)
            coVerify(exactly = 1) { api.login(any()) }
        }

    @Test
    fun `warm profile failure retains content and logout rejects non cancellable late identity`() =
        runTest(dispatcher) {
            val session =
                coordinator(MemoryPreferences(savedSession(clock)), api, clock, backgroundScope)
            val model = MainViewModel(session, student)
            runCurrent()
            coEvery { student.execute() } returns Result.Error(IOError)
            model.refreshProfile(1)
            runCurrent()
            assertEquals(identity, model.state.value.student)
            assertEquals(SessionState.Valid, model.state.value.sessionState)
            assertEquals(IOError, model.state.value.error)
            val release = CompletableDeferred<Unit>()
            coEvery { student.execute() } coAnswers {
                withContext(NonCancellable) { release.await() }; Result.Success(
                identity
            )
            }
            model.refreshProfile(2)
            runCurrent()
            model.onLogout()
            assertEquals(SessionState.Invalid, model.state.value.sessionState)
            assertNull(model.state.value.student)
            release.complete(Unit)
            runCurrent()
            assertNull(model.state.value.student)
            assertEquals(SessionState.Invalid, model.state.value.sessionState)
        }
}
