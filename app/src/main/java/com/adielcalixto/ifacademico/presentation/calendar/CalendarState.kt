package com.adielcalixto.ifacademico.presentation.calendar

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.entities.SpecialDate
import java.time.LocalDate

data class CalendarState(
    val selectedDate: LocalDate,
    val specialDates: List<SpecialDate> = emptyList(),
    val error: Error.DataError? = null,
    val isLoading: Boolean = false
)