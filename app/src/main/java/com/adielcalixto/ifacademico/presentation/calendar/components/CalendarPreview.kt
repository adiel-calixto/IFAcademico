package com.adielcalixto.ifacademico.presentation.calendar.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adielcalixto.ifacademico.domain.entities.SpecialDate
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.calendar.CalendarContent
import com.adielcalixto.ifacademico.presentation.calendar.CalendarState
import com.adielcalixto.ifacademico.presentation.components.*
import java.time.LocalDate

internal val PreviewCalendarDate = LocalDate.of(2026, 10, 19)
internal val PreviewCalendarEvent = SpecialDate(
    startDate = LocalDate.of(2026, 10, 19).atStartOfDay(),
    endDate = LocalDate.of(2026, 10, 23).atStartOfDay(),
    description = "Semana de ciência e tecnologia: pesquisa, inovação e atividades de extensão",
    color = "#287343",
    type = "Eventos Desportivos, Socioculturais e Científicos",
    professorName = "Professora Maria Costa",
    classDescription = "Auditório · Bloco A",
)
internal val PreviewCalendar = CalendarState(
    selectedDate = PreviewCalendarDate.withDayOfMonth(1),
    specialDates = listOf(
        PreviewCalendarEvent.copy(
            startDate = LocalDate.of(2026, 9, 28).atStartOfDay(),
            endDate = LocalDate.of(2026, 10, 2).atStartOfDay(),
            description = "Reajuste de matrícula",
            type = "Reajuste de Matrícula",
            professorName = null,
            classDescription = null,
        ),
        PreviewCalendarEvent.copy(
            startDate = LocalDate.of(2026, 10, 12).atStartOfDay(),
            endDate = LocalDate.of(2026, 10, 12).atStartOfDay(),
            description = "Nossa Senhora Aparecida",
            type = "Feriado Nacional",
            professorName = null,
            classDescription = null,
        ),
        PreviewCalendarEvent,
        PreviewCalendarEvent.copy(
            startDate = LocalDate.of(2026, 10, 21).atStartOfDay(),
            endDate = LocalDate.of(2026, 10, 21).atStartOfDay(),
            description = "Avaliação final de algoritmos",
            type = "Avaliação Final (af)",
            professorName = null,
            classDescription = null,
        ),
        PreviewCalendarEvent.copy(
            startDate = LocalDate.of(2026, 10, 28).atStartOfDay(),
            endDate = LocalDate.of(2026, 10, 28).atStartOfDay(),
            description = "Entrega de diários",
            type = "Entrega de Diário",
            professorName = null,
            classDescription = null,
        ),
    ),
)

@IFCatalogPreviews
@Composable
internal fun CalendarPreview() {
    IFTheme {
        Scaffold(
            topBar = { IFHeader({}) },
            bottomBar = {
                IFBottomNavigation(
                    listOf(
                        IFNavigationItem("Hoje", IFIcon.Home),
                        IFNavigationItem("Notas", IFIcon.Grades),
                        IFNavigationItem("Calendário", IFIcon.Calendar),
                    ), 2, {})
            },
        ) { padding ->
            CalendarContent(
                PreviewCalendar,
                {},
                Modifier.padding(padding),
                today = PreviewCalendarDate
            )
        }
    }
}

@IFCatalogPreviews
@Composable
internal fun EmptyCalendarPreview() {
    IFTheme {
        Surface(color = IFTheme.colors.background) {
            CalendarContent(
                PreviewCalendar.copy(specialDates = emptyList()),
                {},
                today = PreviewCalendarDate
            )
        }
    }
}
