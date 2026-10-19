package com.adielcalixto.ifacademico.presentation.calendar

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.calendar.components.PreviewCalendar
import com.adielcalixto.ifacademico.presentation.calendar.components.PreviewCalendarDate
import com.adielcalixto.ifacademico.presentation.calendar.components.PreviewCalendarEvent
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class CalendarInteractionTest {
    @get:Rule
    val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val locale get() = context.resources.configuration.locales[0]
    private fun label(id: Int) = context.getString(id)
    private fun monthLabel(date: LocalDate) = YearMonth.from(date)
        .format(DateTimeFormatter.ofPattern("LLLL yyyy", locale))

    private fun dayLabel(day: Int, event: Boolean = false, today: Boolean = false): String =
        listOfNotNull(
            PreviewCalendarDate.withDayOfMonth(day)
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)),
            if (today) label(R.string.today_label) else null,
            if (event) label(R.string.special_date_marker) else null,
        ).plus(
            if (event) PreviewCalendar.specialDates.filter {
                val date = PreviewCalendarDate.withDayOfMonth(day)
                date >= it.startDate.toLocalDate() && date <= it.endDate.toLocalDate()
            }.map { it.type }.distinct() else emptyList()
        ).joinToString(", ")

    @Composable
    private fun Host(state: CalendarState, onAction: (CalendarAction) -> Unit = {}) {
        IFTheme { CalendarContent(state, onAction, today = PreviewCalendarDate) }
    }

    @Test
    fun datesAndEventMarkersAreStaticAndWeeksStartOnSunday() {
        compose.setContent { Host(PreviewCalendar) }
        compose.onAllNodes(hasClickAction()).assertCountEquals(2)
        compose.onNodeWithContentDescription(dayLabel(19, event = true, today = true))
            .assertHasNoClickAction()
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Selected))
        compose.onNodeWithContentDescription(dayLabel(1, event = true)).assertHasNoClickAction()
        compose.onNodeWithContentDescription(dayLabel(2, event = true)).assertHasNoClickAction()
        val saturday =
            compose.onNodeWithContentDescription(dayLabel(3)).fetchSemanticsNode().boundsInRoot
        val sunday =
            compose.onNodeWithContentDescription(dayLabel(4)).fetchSemanticsNode().boundsInRoot
        assertEquals("Date cells are square", saturday.width, saturday.height, 1f)
        assertTrue(
            "Sunday begins the next row",
            sunday.left < saturday.left && sunday.top > saturday.top
        )
    }

    @Test
    fun monthArrowsCrossYearBoundaries() {
        var state by mutableStateOf(PreviewCalendar.copy(selectedDate = LocalDate.of(2025, 12, 1)))
        compose.setContent {
            Host(state) { action ->
                if (action is CalendarAction.OnMonthChanged) state =
                    state.copy(selectedDate = action.date)
            }
        }
        compose.onNodeWithContentDescription(label(R.string.next_month)).performClick()
        compose.onNodeWithText(monthLabel(LocalDate.of(2026, 1, 1))).assertIsDisplayed()
        compose.onNodeWithContentDescription(label(R.string.previous_month)).performClick()
        compose.onNodeWithText(monthLabel(LocalDate.of(2025, 12, 1))).assertIsDisplayed()
    }

    @Test
    fun eventRowsUseCompactDatesAndDescriptionsWithoutExtraMetadata() {
        compose.setContent { Host(PreviewCalendar) }
        val crossing = PreviewCalendar.specialDates.first()
        compose.onNode(hasScrollAction()).performScrollToNode(hasText(crossing.description))
        compose.onNodeWithText(crossing.description).assertHasNoClickAction()
        compose.onNodeWithText(crossing.formattedCompactDateRange(locale)).assertIsDisplayed()
        compose.onNodeWithContentDescription(crossing.formattedDateRange(locale)).assertExists()
        compose.onNode(hasScrollAction())
            .performScrollToNode(hasText(PreviewCalendarEvent.description))
        compose.onNodeWithText(PreviewCalendarEvent.description).assertHasNoClickAction()
        compose.onNodeWithText(PreviewCalendarEvent.professorName!!).assertDoesNotExist()
        compose.onNodeWithText(PreviewCalendarEvent.classDescription!!).assertDoesNotExist()
        compose.onNodeWithText(PreviewCalendarEvent.type).assertDoesNotExist()
    }

    @Test
    fun loadingAndFailureKeepMonthControlsAndRetryDoesNotShowStaleMarkers() {
        var state by mutableStateOf(PreviewCalendar.copy(isLoading = true))
        var action: CalendarAction? = null
        compose.setContent { Host(state) { action = it } }
        compose.onNodeWithContentDescription(dayLabel(19, today = true)).assertExists()
        compose.onNodeWithContentDescription(label(R.string.next_month)).assertIsEnabled()
        compose.onNode(hasScrollAction())
            .performScrollToNode(hasText(label(R.string.loading_records)))
        compose.onNodeWithText(label(R.string.loading_records)).assertIsDisplayed()
        compose.runOnIdle { state = state.copy(isLoading = false, error = IOError) }
        compose.onNodeWithText(label(R.string.retry)).performScrollTo().performClick()
        compose.runOnIdle { assertEquals(CalendarAction.Retry, action) }
        compose.onNodeWithText(PreviewCalendarEvent.description).assertDoesNotExist()
        compose.runOnIdle { state = state.copy(error = null, specialDates = emptyList()) }
        compose.onNodeWithText(label(R.string.no_special_dates)).performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun returningFromAnotherPageRestoresScrollAndChangingMonthResetsIt() {
        val events = (1..18).map { PreviewCalendarEvent.copy(description = "Event $it") }
        var state by mutableStateOf(PreviewCalendar.copy(specialDates = events))
        var showingCalendar by mutableStateOf(true)
        compose.setContent {
            val holder = rememberSaveableStateHolder()
            if (showingCalendar) {
                holder.SaveableStateProvider("calendar") { Host(state) }
            } else {
                Text("Profile")
            }
        }
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Event 18"))
        val before = compose.onNodeWithText("Event 18").fetchSemanticsNode().boundsInRoot.top
        compose.runOnIdle { showingCalendar = false }
        compose.onNodeWithText("Profile").assertIsDisplayed()
        compose.runOnIdle { showingCalendar = true }
        compose.onNodeWithText("Event 18").assertIsDisplayed()
        assertEquals(
            before,
            compose.onNodeWithText("Event 18").fetchSemanticsNode().boundsInRoot.top,
            2f
        )
        compose.runOnIdle { state = state.copy(selectedDate = state.selectedDate.plusMonths(1)) }
        compose.onNodeWithText(monthLabel(state.selectedDate)).assertIsDisplayed()
        compose.onNodeWithContentDescription(label(R.string.next_month)).assertIsDisplayed()
    }
}
