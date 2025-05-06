package com.adielcalixto.ifacademico.presentation.calendar

import java.time.LocalDate

sealed class CalendarAction {
    data class OnMonthChanged(val date: LocalDate): CalendarAction()
}