package com.adielcalixto.ifacademico.presentation.diary_list

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.*
import com.adielcalixto.ifacademico.domain.usecases.*
import io.mockk.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.*
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryListViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val periods = mockk<GetPeriodsUseCase>()
    private val diaries = mockk<GetDiariesUseCase>()
    private val exams = mockk<GetExamsUseCase>()
    private val current = Period(2026, 2)
    private val previous = Period(2026, 1)
    private val diary = Diary(1, "Algorithms", 80, "2026", "Teacher", 2, 20, 18, 1, 2, 40, 32, 8)
    private val exam = Exam("Test", null, null, 0f, 1, "A1", 10f, "1")

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        coEvery { periods.execute() } returns Result.Success(listOf(current, previous))
        coEvery { diaries.execute(any(), any(), any()) } returns Result.Success(listOf(diary))
        coEvery { exams.execute(any(), any()) } returns Result.Success(listOf(exam))
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun model() = DiaryListViewModel(periods, diaries, exams)

    @Test
    fun `waits for registration initializes once and shares assessment results with details`() =
        runTest(dispatcher) {
            val model = model()
            runCurrent()
            coVerify(exactly = 0) { periods.execute() }
            model.setRegistrationId(0)
            model.setRegistrationId(42)
            model.setRegistrationId(42)
            runCurrent()
            assertEquals(current, model.state.value.selectedPeriod)
            assertEquals(listOf(exam), model.state.value.assessments[1]!!.exams)
            model.onAction(DiaryListAction.ShowSubject(1))
            assertEquals(GradesSheet.Subject(1), model.state.value.sheet)
            coVerify(exactly = 1) { periods.execute() }
            coVerify(exactly = 1) { diaries.execute(42, 2, 2026) }
            coVerify(exactly = 1) { exams.execute(1, 42) }
        }

    @Test
    fun `empty periods and subjects have successful empty states`() = runTest(dispatcher) {
        coEvery { periods.execute() } returns Result.Success(emptyList())
        val model = model()
        model.setRegistrationId(42)
        runCurrent()
        assertFalse(model.state.value.isLoading)
        assertNull(model.state.value.error)
        assertNull(model.state.value.selectedPeriod)
        coVerify(exactly = 0) { diaries.execute(any(), any(), any()) }
        coEvery { periods.execute() } returns Result.Success(listOf(current))
        coEvery { diaries.execute(any(), any(), any()) } returns Result.Success(emptyList())
        model.onAction(DiaryListAction.LoadViewModelData)
        runCurrent()
        assertEquals(current, model.state.value.selectedPeriod)
        assertTrue(model.state.value.diaries.isEmpty())
        coVerify(exactly = 0) { exams.execute(any(), any()) }
    }

    @Test
    fun `period and subject failures retry independently of assessment data`() =
        runTest(dispatcher) {
            coEvery { periods.execute() } returns Result.Error(IOError)
            val model = model()
            model.setRegistrationId(42)
            runCurrent()
            assertEquals(IOError, model.state.value.error)
            coEvery { periods.execute() } returns Result.Success(listOf(current))
            coEvery { diaries.execute(any(), any(), any()) } returns Result.Error(IOError)
            model.onAction(DiaryListAction.LoadViewModelData)
            runCurrent()
            assertEquals(IOError, model.state.value.error)
            coEvery { diaries.execute(any(), any(), any()) } returns Result.Success(listOf(diary))
            model.onAction(DiaryListAction.LoadViewModelData)
            runCurrent()
            assertNull(model.state.value.error)
            assertFalse(model.state.value.isLoading)
            assertEquals(listOf(diary), model.state.value.diaries)
        }

    @Test
    fun `subjects display while counts load and a failed subject can retry`() =
        runTest(dispatcher) {
            val delayed = CompletableDeferred<Result<List<Exam>, Error.DataError>>()
            coEvery { diaries.execute(any(), any(), any()) } returns Result.Success(
                listOf(
                    diary,
                    diary.copy(id = 2)
                )
            )
            coEvery { exams.execute(1, 42) } coAnswers { delayed.await() }
            coEvery { exams.execute(2, 42) } returns Result.Error(IOError)
            val model = model()
            model.setRegistrationId(42)
            runCurrent()
            assertFalse(model.state.value.isLoading)
            assertEquals(2, model.state.value.diaries.size)
            assertTrue(model.state.value.assessments[1]!!.isLoading)
            assertEquals(IOError, model.state.value.assessments[2]!!.error)
            assertNull(model.state.value.error)
            model.onAction(DiaryListAction.ShowSubject(2))
            coEvery { exams.execute(2, 42) } returns Result.Success(emptyList())
            model.onAction(DiaryListAction.RetryAssessments(2))
            model.onAction(DiaryListAction.RetryAssessments(2))
            runCurrent()
            assertEquals(GradesSheet.Subject(2), model.state.value.sheet)
            assertEquals(SubjectAssessments(emptyList(), false), model.state.value.assessments[2])
            coVerify(exactly = 2) { exams.execute(2, 42) }
            delayed.complete(Result.Success(listOf(exam)))
            runCurrent()
            assertEquals(listOf(exam), model.state.value.assessments[1]!!.exams)
        }

    @Test
    fun `switching periods closes sheets and ignores obsolete assessment responses`() =
        runTest(dispatcher) {
            val delayed = CompletableDeferred<Result<List<Exam>, Error.DataError>>()
            coEvery {
                exams.execute(
                    1,
                    42
                )
            } coAnswers { withContext(NonCancellable) { delayed.await() } }
            val model = model()
            model.setRegistrationId(42)
            runCurrent()
            model.onAction(DiaryListAction.ShowPeriods)
            coEvery { exams.execute(1, 42) } returns Result.Success(emptyList())
            model.onAction(DiaryListAction.SelectPeriod(previous))
            runCurrent()
            assertEquals(previous, model.state.value.selectedPeriod)
            assertEquals(GradesSheet.Closed, model.state.value.sheet)
            delayed.complete(Result.Success(listOf(exam)))
            runCurrent()
            assertTrue(model.state.value.assessments[1]!!.exams.isEmpty())
            coVerify(exactly = 1) { periods.execute() }
            coVerify(exactly = 1) { diaries.execute(42, 1, 2026) }
            model.onAction(DiaryListAction.SelectPeriod(previous))
            runCurrent()
            coVerify(exactly = 1) { diaries.execute(42, 1, 2026) }
        }

    @Test
    fun `obsolete subject response cannot overwrite new registration`() = runTest(dispatcher) {
        val delayed = CompletableDeferred<Result<List<Diary>, Error.DataError>>()
        coEvery {
            diaries.execute(
                42,
                2,
                2026
            )
        } coAnswers { withContext(NonCancellable) { delayed.await() } }
        val model = model()
        model.setRegistrationId(42)
        runCurrent()
        model.setRegistrationId(43)
        runCurrent()
        delayed.complete(Result.Success(listOf(diary.copy(name = "Obsolete"))))
        runCurrent()
        assertEquals(43, model.state.value.registrationId)
        assertEquals("Algorithms", model.state.value.diaries.single().name)
        coVerify(exactly = 0) { exams.execute(1, 42) }
    }

    @Test
    fun `assessment fetching is bounded to four simultaneous calls`() = runTest(dispatcher) {
        val release = CompletableDeferred<Unit>()
        var active = 0
        var maximum = 0
        coEvery {
            diaries.execute(
                any(),
                any(),
                any()
            )
        } returns Result.Success((1..12).map { diary.copy(id = it) })
        coEvery { exams.execute(any(), any()) } coAnswers {
            active++
            maximum = maxOf(maximum, active)
            release.await()
            active--
            Result.Success(emptyList())
        }
        val model = model()
        model.setRegistrationId(42)
        runCurrent()
        assertEquals(4, active)
        release.complete(Unit)
        runCurrent()
        assertEquals(4, maximum)
        assertTrue(model.state.value.assessments.values.all { !it.isLoading })
    }

    @Test
    fun `foreground reload preserves period sheet and grades through failure then refreshes visible details`() =
        runTest(dispatcher) {
            val model = model()
            model.setRegistrationId(42)
            runCurrent()
            model.onAction(DiaryListAction.SelectPeriod(previous))
            runCurrent()
            model.onAction(DiaryListAction.ShowSubject(1))
            coEvery { diaries.execute(42, 1, 2026) } returns Result.Error(IOError)
            model.refreshIfStale(1)
            runCurrent()
            assertEquals(previous, model.state.value.selectedPeriod)
            assertEquals(GradesSheet.Subject(1), model.state.value.sheet)
            assertEquals(listOf(diary), model.state.value.diaries)
            assertEquals(listOf(exam), model.state.value.assessments[1]!!.exams)
            assertFalse(model.state.value.isLoading)
            coEvery {
                diaries.execute(
                    42,
                    1,
                    2026
                )
            } returns Result.Success(listOf(diary.copy(name = "Updated subject")))
            coEvery { exams.execute(1, 42) } returns Result.Success(listOf(exam.copy(grade = 9f)))
            model.onAction(DiaryListAction.LoadViewModelData)
            runCurrent()
            assertNull(model.state.value.error)
            assertEquals("Updated subject", model.state.value.diaries.single().name)
            assertEquals(9f, model.state.value.assessments[1]!!.exams.single().grade)
            assertEquals(GradesSheet.Subject(1), model.state.value.sheet)
        }
}
