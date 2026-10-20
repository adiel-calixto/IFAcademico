package com.adielcalixto.ifacademico.presentation.calendar

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.calendar.components.IFCalendarDayMarker
import com.adielcalixto.ifacademico.presentation.calendar.components.IFMonthGrid
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class CalendarMotionTest {
    @get:Rule
    val compose = createComposeRule()

    private fun dateLabel(date: LocalDate): String {
        val locale = InstrumentationRegistry.getInstrumentation().targetContext
            .resources.configuration.locales[0]
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale))
    }

    @Test
    fun nextAndPreviousMoveInOppositeDirectionsAndHideOutgoingDates() {
        val october = YearMonth.of(2026, 10)
        val november = october.plusMonths(1)
        var month by mutableStateOf(october)
        compose.setContent {
            IFTheme {
                IFMonthGrid(month, null, emptyMap(), {}, {})
            }
        }
        val octoberX = compose.onNodeWithContentDescription(dateLabel(october.atDay(1)))
            .fetchSemanticsNode().positionInRoot.x
        compose.mainClock.autoAdvance = false
        compose.runOnUiThread { month = november }
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeByFrame()
        val enteringX = compose.onNodeWithContentDescription(dateLabel(november.atDay(1)))
            .fetchSemanticsNode().positionInRoot.x
        compose.onNodeWithContentDescription(dateLabel(october.atDay(1))).assertDoesNotExist()
        compose.mainClock.advanceTimeBy(240)
        val settledX = compose.onNodeWithContentDescription(dateLabel(november.atDay(1)))
            .fetchSemanticsNode().positionInRoot.x
        assertTrue("Next month enters from the right", enteringX > settledX)

        compose.runOnUiThread { month = october }
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeByFrame()
        val returningX = compose.onNodeWithContentDescription(dateLabel(october.atDay(1)))
            .fetchSemanticsNode().positionInRoot.x
        assertTrue("Previous month enters from the left", returningX < octoberX)
        compose.onNodeWithContentDescription(dateLabel(november.atDay(1))).assertDoesNotExist()
        compose.mainClock.autoAdvance = true
    }

    @Test
    fun markersUpdateWithoutMotionAndRapidMonthChangesSettleOnLatestMonth() {
        var month by mutableStateOf(YearMonth.of(2026, 12))
        var markers by mutableStateOf<Map<LocalDate, IFCalendarDayMarker>>(emptyMap())
        compose.setContent {
            IFTheme {
                IFMonthGrid(month, null, markers, {}, {})
            }
        }
        val date = month.atDay(1)
        val before = compose.onNodeWithContentDescription(dateLabel(date))
            .fetchSemanticsNode().positionInRoot.x
        compose.mainClock.autoAdvance = false
        compose.runOnUiThread {
            markers = mapOf(date to IFCalendarDayMarker(Color.White, Color.Black, listOf("Assessment")))
        }
        compose.mainClock.advanceTimeByFrame()
        val marked = compose.onNode(hasContentDescription(dateLabel(date), substring = true))
        assertEquals(before, marked.fetchSemanticsNode().positionInRoot.x, 0f)
        compose.runOnUiThread { month = month.plusMonths(1); markers = emptyMap() }
        compose.mainClock.advanceTimeBy(64)
        compose.runOnUiThread { month = month.plusMonths(1) }
        compose.mainClock.advanceTimeBy(64)
        compose.runOnUiThread { month = month.minusMonths(1) }
        compose.mainClock.autoAdvance = true
        compose.onNodeWithContentDescription(dateLabel(LocalDate.of(2027, 1, 1))).assertExists()
        compose.onNodeWithContentDescription(dateLabel(LocalDate.of(2027, 2, 1))).assertDoesNotExist()
        compose.onNodeWithContentDescription(dateLabel(date)).assertDoesNotExist()
    }
}
