package com.adielcalixto.ifacademico.presentation.calendar

import com.adielcalixto.ifacademico.domain.entities.SpecialDate
import com.adielcalixto.ifacademico.presentation.IFColors
import com.adielcalixto.ifacademico.presentation.calendar.components.IFCalendarDayMarker
import java.time.LocalDate
import java.time.YearMonth

internal fun calendarEventMarker(type: String, colors: IFColors): IFCalendarDayMarker {
    val (background, foreground) = when (type) {
        "Feriado Nacional", "Feriado Municipal", "Férias", "Recesso Escolar" ->
            colors.notice to colors.onNotice

        "Etapa Letiva", "Início/fim de Período Letivo", "Reajuste de Matrícula",
        "Encontro Pedagógico", "Sábado Letivo" -> colors.primaryContainer to colors.primary

        "Eventos Desportivos, Socioculturais e Científicos", "Outras Datas Acadêmicas" ->
            colors.calendarEventSurface to colors.onCalendarEvent

        "Avaliação Final (af)" -> colors.calendarAssessmentSurface to colors.onCalendarAssessment
        else -> colors.surfaceSubtle to colors.secondary
    }
    return IFCalendarDayMarker(background, foreground, listOf(type).filter { it.isNotBlank() })
}

/** Input follows display order: the last overlapping event supplies the color, all types stay readable. */
internal fun calendarDayMarkers(
    dates: List<SpecialDate>,
    month: YearMonth,
    colors: IFColors,
): Map<LocalDate, IFCalendarDayMarker> = buildMap {
    dates.forEach { event ->
        val marker = calendarEventMarker(event.type, colors)
        var date = maxOf(event.startDate.toLocalDate(), month.atDay(1))
        val end = minOf(event.endDate.toLocalDate(), month.atEndOfMonth())
        while (date <= end) {
            val types = (get(date)?.eventTypes.orEmpty() + marker.eventTypes).distinct()
            put(date, marker.copy(eventTypes = types))
            date = date.plusDays(1)
        }
    }
}
