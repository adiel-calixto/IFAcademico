package com.adielcalixto.ifacademico.presentation.calendar

import com.adielcalixto.ifacademico.domain.entities.SpecialDate
import com.adielcalixto.ifacademico.presentation.LightColors
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

internal fun calendarEvent(
    start: String,
    end: String = start,
    description: String = "Event",
    type: String = "Feriado Nacional",
) = SpecialDate(
    LocalDate.parse(start).atStartOfDay(), LocalDate.parse(end).atStartOfDay(),
    description, "#287343", type, null, null,
)

class CalendarFormattingTest {
    @Test
    fun `compact rows use day numbers and identify months only for cross month ranges`() {
        assertEquals("3", calendarEvent("2026-01-03").formattedCompactDateRange(Locale.US))
        assertEquals(
            "3–5",
            calendarEvent("2026-01-03", "2026-01-05").formattedCompactDateRange(Locale.US)
        )
        assertEquals(
            "30 Dec–3 Jan",
            calendarEvent("2025-12-30", "2026-01-03").formattedCompactDateRange(Locale.US)
        )
    }

    @Test
    fun `includes overlapping events once sorts them and excludes classes and other months`() {
        val spanning = calendarEvent("2025-12-30", "2026-02-02")
        val a = calendarEvent("2026-01-10", description = "A")
        val b = calendarEvent("2026-01-10", description = "B")
        val dates = listOf(
            b, calendarEvent("2026-02-01"), spanning, a,
            calendarEvent("2026-01-11", type = "Aula"), calendarEvent("2025-12-29")
        )
        assertEquals(listOf(spanning, a, b), specialDatesForMonth(dates, YearMonth.of(2026, 1)))
    }

    @Test
    fun `clips markers at both month boundaries across years and merges overlapping days`() {
        val month = YearMonth.of(2026, 1)
        val dates = listOf(
            calendarEvent("2025-12-30", "2026-01-03"),
            calendarEvent("2026-01-02", "2026-01-04"), calendarEvent("2026-01-31", "2026-02-02")
        )
        assertEquals(
            setOf(1, 2, 3, 4, 31).map { month.atDay(it) }.toSet(),
            calendarDayMarkers(dates, month, LightColors).keys
        )
    }

    @Test
    fun `leap February marks all 29 days for a spanning event`() {
        val month = YearMonth.of(2024, 2)
        assertEquals(
            (1..29).map { month.atDay(it) }.toSet(),
            calendarDayMarkers(
                listOf(calendarEvent("2024-01-31", "2024-03-01")),
                month,
                LightColors
            ).keys
        )
        assertTrue(
            calendarDayMarkers(
                listOf(calendarEvent("2024-03-01")),
                month,
                LightColors
            ).isEmpty()
        )
    }

    @Test
    fun `formats full cross year ranges without clipping and single days without a range`() {
        val range = calendarEvent("2025-12-30", "2026-01-03")
        assertEquals("Dec 30, 2025 – Jan 3, 2026", range.formattedDateRange(Locale.US))
        assertEquals("Jan 3, 2026", calendarEvent("2026-01-03").formattedDateRange(Locale.US))
        assertEquals(
            "3 de jan. de 2026",
            calendarEvent("2026-01-03").formattedDateRange(Locale.forLanguageTag("pt-BR"))
        )
    }
}
