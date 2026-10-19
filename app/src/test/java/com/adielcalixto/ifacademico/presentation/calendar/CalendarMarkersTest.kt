package com.adielcalixto.ifacademico.presentation.calendar

import com.adielcalixto.ifacademico.presentation.DarkColors
import com.adielcalixto.ifacademico.presentation.LightColors
import org.junit.Assert.*
import org.junit.Test
import java.time.YearMonth

class CalendarMarkersTest {
    @Test
    fun `known API event types use five distinct semantic palettes in each theme`() {
        listOf(LightColors, DarkColors).forEach { colors ->
            val groups = listOf(
                Triple(
                    listOf("Feriado Nacional", "Feriado Municipal", "Férias", "Recesso Escolar"),
                    colors.notice, colors.onNotice
                ),
                Triple(
                    listOf(
                        "Etapa Letiva", "Início/fim de Período Letivo", "Reajuste de Matrícula",
                        "Encontro Pedagógico", "Sábado Letivo"
                    ), colors.primaryContainer, colors.primary
                ),
                Triple(
                    listOf(
                        "Eventos Desportivos, Socioculturais e Científicos",
                        "Outras Datas Acadêmicas"
                    ),
                    colors.calendarEventSurface, colors.onCalendarEvent
                ),
                Triple(
                    listOf("Avaliação Final (af)"),
                    colors.calendarAssessmentSurface,
                    colors.onCalendarAssessment
                ),
                Triple(
                    listOf("Entrega de Diário", "Reunião de Colegiado de Curso"),
                    colors.surfaceSubtle,
                    colors.secondary
                ),
            )
            assertEquals(5, groups.map { it.second }.distinct().size)
            groups.forEach { (types, background, foreground) ->
                types.forEach { type ->
                    val marker = calendarEventMarker(type, colors)
                    assertEquals(type, background, marker.background)
                    assertEquals(type, foreground, marker.foreground)
                    assertEquals(listOf(type), marker.eventTypes)
                }
            }
        }
    }

    @Test
    fun `unknown types use a neutral fallback and retain their supplied label`() {
        listOf(LightColors, DarkColors).forEach { colors ->
            val marker = calendarEventMarker("New academic record", colors)
            assertEquals(colors.surfaceSubtle, marker.background)
            assertEquals(colors.secondary, marker.foreground)
            assertEquals(listOf("New academic record"), marker.eventTypes)
            assertTrue(calendarEventMarker("", colors).eventTypes.isEmpty())
        }
    }

    @Test
    fun `last listed overlap supplies color while all distinct types remain accessible`() {
        val month = YearMonth.of(2026, 1)
        val span = calendarEvent("2025-12-30", "2026-01-04", type = "Etapa Letiva")
        val assessment = calendarEvent("2026-01-02", type = "Avaliação Final (af)")
        val markers = calendarDayMarkers(listOf(span, assessment, assessment), month, LightColors)
        assertEquals((1..4).map { month.atDay(it) }.toSet(), markers.keys)
        assertEquals(LightColors.primaryContainer, markers.getValue(month.atDay(1)).background)
        assertEquals(
            LightColors.calendarAssessmentSurface,
            markers.getValue(month.atDay(2)).background
        )
        assertEquals(
            listOf("Etapa Letiva", "Avaliação Final (af)"),
            markers.getValue(month.atDay(2)).eventTypes
        )
        assertEquals(LightColors.primaryContainer, markers.getValue(month.atDay(3)).background)
    }
}
