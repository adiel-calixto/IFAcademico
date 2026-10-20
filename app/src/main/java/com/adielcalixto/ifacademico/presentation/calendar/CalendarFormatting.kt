package com.adielcalixto.ifacademico.presentation.calendar

import com.adielcalixto.ifacademico.domain.entities.SpecialDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

internal fun specialDatesForMonth(dates: List<SpecialDate>, month: YearMonth): List<SpecialDate> =
    dates.filter {
        it.type != "Aula" &&
                it.startDate.toLocalDate() <= month.atEndOfMonth() &&
                it.endDate.toLocalDate() >= month.atDay(1)
    }.sortedWith(compareBy<SpecialDate> { it.startDate }.thenBy { it.description })

internal fun SpecialDate.formattedDateRange(locale: Locale): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
    val start = startDate.toLocalDate()
    val end = endDate.toLocalDate()
    return if (start == end) start.format(formatter) else "${start.format(formatter)} – ${
        end.format(
            formatter
        )
    }"
}

internal fun SpecialDate.formattedCompactDateRange(locale: Locale): String {
    val start = startDate.toLocalDate()
    val end = endDate.toLocalDate()
    val pattern = if (YearMonth.from(start) == YearMonth.from(end)) "d" else "d MMM"
    val formatter = DateTimeFormatter.ofPattern(pattern, locale)
    return if (start == end) {
        start.format(formatter)
    } else {
        "${start.format(formatter)}–${end.format(formatter)}"
    }
}
