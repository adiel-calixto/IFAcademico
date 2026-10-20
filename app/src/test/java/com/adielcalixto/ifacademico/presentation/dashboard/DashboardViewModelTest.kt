package com.adielcalixto.ifacademico.presentation.dashboard

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
class DashboardViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val coefficients = mockk<GetPerformanceCoefficientsUseCase>()
    private val today = mockk<GetTimeTableUseCase>()
    private val periods = mockk<GetPeriodsUseCase>()
    private val weekly = mockk<GetIndividualTimeTableUseCase>()
    private val course =
        TimeTableClass(1, "Algorithms", "Teacher", 2, "08:00", "09:40", "204", false, 2)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        coEvery { coefficients.execute() } returns Result.Success(
            PerformanceCoefficients(
                0f,
                7.8f,
                9f
            )
        )
        coEvery { today.execute() } returns Result.Success(
            TimeTable(
                listOf(course),
                "2026-10-19",
                "09:00"
            )
        )
        coEvery { periods.execute() } returns Result.Success(
            listOf(
                Period(2026, 2),
                Period(2026, 1)
            )
        )
        coEvery {
            weekly.execute(
                2026,
                2
            )
        } returns Result.Success(IndividualTimeTable(mapOf(2 to listOf(course)), 2))
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun model() = DashboardViewModel(coefficients, today, periods, weekly)

    @Test
    fun `loads home and retains a supplied zero coefficient`() = runTest(dispatcher) {
        coEvery { today.execute() } returns Result.Success(
            TimeTable(
                listOf(
                    course.copy(startTime = "15:00"),
                    course
                ), "", ""
            )
        )
        val model = model()
        assertTrue(model.state.value.isLoading)
        runCurrent()
        assertFalse(model.state.value.isLoading)
        assertNull(model.state.value.error)
        assertEquals(0f, model.state.value.performanceCoefficients.studentPerformance)
        assertEquals(
            listOf("08:00", "15:00"),
            model.state.value.timeTable.classes.map { it.startTime })
    }

    @Test
    fun `home failure retries without retaining error`() = runTest(dispatcher) {
        coEvery { today.execute() } returns Result.Error(IOError)
        val model = model()
        runCurrent()
        assertEquals(IOError, model.state.value.error)
        coEvery { today.execute() } returns Result.Success(TimeTable(emptyList(), "", ""))
        model.onAction(DashboardAction.LoadViewModelData)
        runCurrent()
        assertNull(model.state.value.error)
        assertFalse(model.state.value.isLoading)
    }

    @Test
    fun `opens immediately loads first API period once and reuses success`() = runTest(dispatcher) {
        val model = model()
        runCurrent()
        model.onAction(DashboardAction.ShowIndividualTimeTable)
        model.onAction(DashboardAction.ShowIndividualTimeTable)
        assertEquals(DashboardSheet.Timetable, model.state.value.sheet)
        assertTrue(model.state.value.timetableLoading)
        runCurrent()
        assertEquals(Period(2026, 2), model.state.value.currentPeriod)
        model.onAction(DashboardAction.HideIndividualTimeTable)
        model.onAction(DashboardAction.ShowIndividualTimeTable)
        runCurrent()
        coVerify(exactly = 1) { weekly.execute(2026, 2) }
        coVerify(exactly = 0) { weekly.execute(2026, 1) }
    }

    @Test
    fun `empty periods leave an empty sheet without requesting a timetable`() =
        runTest(dispatcher) {
            coEvery { periods.execute() } returns Result.Success(emptyList())
            val model = model()
            model.onAction(DashboardAction.ShowIndividualTimeTable)
            runCurrent()
            assertNull(model.state.value.currentPeriod)
            assertFalse(model.state.value.timetableLoading)
            assertNull(model.state.value.timetableError)
            coVerify(exactly = 0) { weekly.execute(any(), any()) }
        }

    @Test
    fun `period and timetable failures stay in sheet and can retry`() = runTest(dispatcher) {
        coEvery { periods.execute() } returns Result.Error(IOError)
        val model = model()
        model.onAction(DashboardAction.ShowIndividualTimeTable)
        runCurrent()
        assertEquals(IOError, model.state.value.timetableError)
        assertNull(model.state.value.error)
        coEvery { periods.execute() } returns Result.Success(listOf(Period(2026, 2)))
        coEvery { weekly.execute(2026, 2) } returns Result.Error(IOError)
        model.onAction(DashboardAction.RetryTimetable)
        runCurrent()
        assertEquals(IOError, model.state.value.timetableError)
        coEvery { weekly.execute(2026, 2) } returns Result.Success(
            IndividualTimeTable(
                emptyMap(),
                1
            )
        )
        model.onAction(DashboardAction.RetryTimetable)
        runCurrent()
        assertNull(model.state.value.timetableError)
        assertEquals(1, model.state.value.selectedWeekday)
    }

    @Test
    fun `late response after dismissal cannot overwrite a new request`() = runTest(dispatcher) {
        val delayed =
            CompletableDeferred<Result<IndividualTimeTable, com.adielcalixto.ifacademico.domain.Error.DataError>>()
        coEvery {
            weekly.execute(
                2026,
                2
            )
        } coAnswers { withContext(NonCancellable) { delayed.await() } }
        val model = model()
        model.onAction(DashboardAction.ShowIndividualTimeTable)
        runCurrent()
        model.onAction(DashboardAction.HideIndividualTimeTable)
        coEvery { weekly.execute(2026, 2) } returns Result.Success(
            IndividualTimeTable(
                emptyMap(),
                7
            )
        )
        model.onAction(DashboardAction.ShowIndividualTimeTable)
        runCurrent()
        delayed.complete(Result.Success(IndividualTimeTable(emptyMap(), 5)))
        runCurrent()
        assertEquals(DashboardSheet.Timetable, model.state.value.sheet)
        assertEquals(7, model.state.value.selectedWeekday)
        assertFalse(model.state.value.timetableLoading)
    }

    @Test
    fun `home class details close`() = runTest(dispatcher) {
        val model = model()
        model.onAction(DashboardAction.ShowClass(course))
        assertEquals(DashboardSheet.ClassDetail(course), model.state.value.sheet)
        model.onAction(DashboardAction.HideIndividualTimeTable)
        assertEquals(DashboardSheet.Closed, model.state.value.sheet)
    }

    @Test
    fun `invalid initial weekday defaults to Monday`() = runTest(dispatcher) {
        coEvery { weekly.execute(2026, 2) } returns Result.Success(
            IndividualTimeTable(
                emptyMap(),
                0
            )
        )
        val model = model()
        model.onAction(DashboardAction.ShowIndividualTimeTable)
        runCurrent()
        assertEquals(2, model.state.value.selectedWeekday)
        model.onAction(DashboardAction.SelectWeekday(8))
        assertEquals(2, model.state.value.selectedWeekday)
    }

    @Test
    fun `foreground refresh preserves content on failure and refreshes stale timetable when reopened`() =
        runTest(dispatcher) {
            val model = model()
            runCurrent()
            model.refreshIfStale(0)
            runCurrent()
            coVerify(exactly = 1) { today.execute() }
            model.onAction(DashboardAction.ShowIndividualTimeTable)
            runCurrent()
            model.onAction(DashboardAction.SelectWeekday(6))
            model.onAction(DashboardAction.HideIndividualTimeTable)
            coEvery { today.execute() } returns Result.Error(IOError)
            model.refreshIfStale(1)
            model.refreshIfStale(1)
            runCurrent()
            assertTrue(model.state.value.hasContent)
            assertEquals(listOf(course), model.state.value.timeTable.classes)
            assertEquals(IOError, model.state.value.error)
            assertFalse(model.state.value.isLoading)
            model.onAction(DashboardAction.ShowIndividualTimeTable)
            runCurrent()
            coVerify(exactly = 2) { weekly.execute(2026, 2) }
            assertEquals(6, model.state.value.selectedWeekday)
        }
}
