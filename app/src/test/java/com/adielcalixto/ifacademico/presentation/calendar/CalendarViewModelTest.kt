package com.adielcalixto.ifacademico.presentation.calendar

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.SpecialDate
import com.adielcalixto.ifacademico.domain.usecases.GetSpecialDatesUseCase
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
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val dates = mockk<GetSpecialDatesUseCase>()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        coEvery { dates.execute(any(), any()) } returns Result.Success(emptyList())
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts at current month and queries its full bounds`() = runTest(dispatcher) {
        val model = CalendarViewModel(dates)
        val today = LocalDate.now()
        assertEquals(today.withDayOfMonth(1), model.state.value.selectedDate)
        assertTrue(model.state.value.isLoading)
        runCurrent()
        coVerify(exactly = 1) {
            dates.execute(
                today.withDayOfMonth(1),
                today.withDayOfMonth(today.lengthOfMonth())
            )
        }
        assertFalse(model.state.value.isLoading)
    }

    @Test
    fun `navigates beyond the old window through year boundaries and leap February`() =
        runTest(dispatcher) {
            val model = CalendarViewModel(dates)
            runCurrent()
            for (date in listOf("2023-12-01", "2024-01-01", "2024-02-01", "2024-03-01")) {
                val month = LocalDate.parse(date)
                model.onAction(CalendarAction.OnMonthChanged(month.withDayOfMonth(15)))
                runCurrent()
                assertEquals(month, model.state.value.selectedDate)
                coVerify { dates.execute(month, month.withDayOfMonth(month.lengthOfMonth())) }
            }
            coVerify { dates.execute(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29)) }
        }

    @Test
    fun `filters and sorts returned records and clears old records on month change`() =
        runTest(dispatcher) {
            val first = calendarEvent("2024-02-01")
            val last = calendarEvent("2024-02-20")
            coEvery { dates.execute(LocalDate.of(2024, 2, 1), any()) } returns Result.Success(
                listOf(
                    last,
                    calendarEvent("2024-03-01"),
                    calendarEvent("2024-02-02", type = "Aula"),
                    first,
                )
            )
            val model = CalendarViewModel(dates)
            model.onAction(CalendarAction.OnMonthChanged(LocalDate.of(2024, 2, 1)))
            runCurrent()
            assertEquals(listOf(first, last), model.state.value.specialDates)
            model.onAction(CalendarAction.OnMonthChanged(LocalDate.of(2024, 3, 1)))
            assertTrue(model.state.value.isLoading)
            assertTrue(model.state.value.specialDates.isEmpty())
            runCurrent()
        }

    @Test
    fun `failure retries current month clearing error and ignores duplicate retries`() =
        runTest(dispatcher) {
            coEvery { dates.execute(any(), any()) } returns Result.Error(IOError)
            val model = CalendarViewModel(dates)
            runCurrent()
            assertEquals(IOError, model.state.value.error)
            coEvery { dates.execute(any(), any()) } returns Result.Success(emptyList())
            model.onAction(CalendarAction.Retry)
            assertNull(model.state.value.error)
            assertTrue(model.state.value.isLoading)
            model.onAction(CalendarAction.Retry)
            runCurrent()
            assertFalse(model.state.value.isLoading)
            assertNull(model.state.value.error)
            coVerify(exactly = 2) { dates.execute(any(), any()) }
        }

    @Test
    fun `late cancelled response cannot replace newer month or its loading state`() =
        runTest(dispatcher) {
            val delayed = CompletableDeferred<Result<List<SpecialDate>, Error.DataError>>()
            coEvery { dates.execute(LocalDate.of(2024, 1, 1), any()) } coAnswers {
                withContext(NonCancellable) { delayed.await() }
            }
            val fresh = calendarEvent("2024-02-10")
            coEvery {
                dates.execute(
                    LocalDate.of(2024, 2, 1),
                    any()
                )
            } returns Result.Success(listOf(fresh))
            val model = CalendarViewModel(dates)
            model.onAction(CalendarAction.OnMonthChanged(LocalDate.of(2024, 1, 1)))
            runCurrent()
            model.onAction(CalendarAction.OnMonthChanged(LocalDate.of(2024, 2, 1)))
            runCurrent()
            delayed.complete(Result.Error(IOError))
            runCurrent()
            assertEquals(LocalDate.of(2024, 2, 1), model.state.value.selectedDate)
            assertEquals(listOf(fresh), model.state.value.specialDates)
            assertNull(model.state.value.error)
            assertFalse(model.state.value.isLoading)
        }
}
