package com.adielcalixto.ifacademico.presentation.dashboard

import com.adielcalixto.ifacademico.domain.entities.TimeTableClass
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.util.Locale

internal fun coefficientLabel(value: Float, locale: Locale): String =
    NumberFormat.getNumberInstance(locale).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(value)

internal fun academicDate(value: String): LocalDate? {
    for (format in listOf(DateTimeFormatter.ISO_LOCAL_DATE, DateTimeFormatter.ofPattern("dd/MM/uuuu"))) {
        try {
            return LocalDate.parse(value.trim(), format.withResolverStyle(ResolverStyle.STRICT))
        } catch (_: DateTimeParseException) {
            // Older servers supply Brazilian dates instead of ISO dates.
        }
    }
    return null
}

internal fun dateLabel(value: String, locale: Locale): String? =
    academicDate(value)?.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))
        ?: value.takeIf { it.isNotBlank() }

internal fun serverTimeLabel(value: String, locale: Locale): String? {
    val time = try {
        OffsetDateTime.parse(value).toLocalTime()
    } catch (_: DateTimeParseException) {
        try {
            LocalDateTime.parse(value).toLocalTime()
        } catch (_: DateTimeParseException) {
            try {
                LocalTime.parse(value)
            } catch (_: DateTimeParseException) {
                return null
            }
        }
    }
    return time.format(DateTimeFormatter.ofPattern("HH:mm", locale))
}

internal fun sortedClasses(classes: List<TimeTableClass>): List<TimeTableClass> =
    classes.sortedBy { course ->
        try {
            LocalTime.parse(course.startTime).toSecondOfDay()
        } catch (_: DateTimeParseException) {
            Int.MAX_VALUE
        }
    }

// The API uses Calendar numbering: Sunday = 1, Monday = 2.
internal val timetableWeekdays = listOf(2, 3, 4, 5, 6, 7, 1)
