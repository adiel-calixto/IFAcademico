package com.adielcalixto.ifacademico.presentation.dashboard

import java.time.LocalDate
import java.util.Locale
import org.junit.Assert.*
import org.junit.Test

class DashboardFormattingTest {
    @Test
    fun `dates accept ISO and Brazilian formats without inventing missing dates`() {
        assertEquals(LocalDate.of(2026, 10, 19), academicDate("2026-10-19"))
        assertEquals(LocalDate.of(2026, 10, 19), academicDate("19/10/2026"))
        assertTrue(dateLabel("2026-10-19", Locale.US)!!.contains("Monday"))
        assertTrue(dateLabel("19/10/2026", Locale.forLanguageTag("pt-BR"))!!.contains("segunda"))
        assertEquals("unrecognized date", dateLabel("unrecognized date", Locale.US))
        assertNull(dateLabel(" ", Locale.US))
        assertNull(academicDate("30/02/2026"))
    }

    @Test
    fun `numbers keep supplied zeros and local decimal separators`() {
        assertEquals("0.00", coefficientLabel(0f, Locale.US))
        assertEquals("8,72", coefficientLabel(8.72f, Locale.forLanguageTag("pt-BR")))
    }

    @Test
    fun `server time always uses 24 hour format and retains supplied offset wall time`() {
        assertEquals("09:41", serverTimeLabel("2026-10-19T09:41:00-03:00", Locale.US))
        assertEquals("21:05", serverTimeLabel("2026-10-19T21:05:00-03:00", Locale.US))
        assertEquals("00:00", serverTimeLabel("00:00:00", Locale.forLanguageTag("pt-BR")))
        assertNull(serverTimeLabel("", Locale.US))
        assertNull(serverTimeLabel("invalid", Locale.US))
    }

    @Test
    fun `weekday order includes both weekend days in API numbering`() {
        assertEquals(listOf(2, 3, 4, 5, 6, 7, 1), timetableWeekdays)
    }
}
