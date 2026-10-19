package com.adielcalixto.ifacademico.presentation.dashboard

import com.adielcalixto.ifacademico.domain.entities.TimeTableClass

sealed interface DashboardAction {
    data object LoadViewModelData : DashboardAction
    data object ShowIndividualTimeTable : DashboardAction
    data object HideIndividualTimeTable : DashboardAction
    data object RetryTimetable : DashboardAction
    data class SelectWeekday(val weekday: Int) : DashboardAction
    data class ShowClass(val course: TimeTableClass) : DashboardAction
}
