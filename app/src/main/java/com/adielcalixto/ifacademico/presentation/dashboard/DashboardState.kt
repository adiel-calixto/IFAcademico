package com.adielcalixto.ifacademico.presentation.dashboard

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.entities.IndividualTimeTable
import com.adielcalixto.ifacademico.domain.entities.PerformanceCoefficients
import com.adielcalixto.ifacademico.domain.entities.Period
import com.adielcalixto.ifacademico.domain.entities.TimeTable
import com.adielcalixto.ifacademico.domain.entities.TimeTableClass

sealed interface DashboardSheet {
    data object Closed : DashboardSheet
    data object Timetable : DashboardSheet
    data class ClassDetail(val course: TimeTableClass) : DashboardSheet
}

data class DashboardState(
    val timeTable: TimeTable = TimeTable(emptyList(), "", ""),
    val performanceCoefficients: PerformanceCoefficients = PerformanceCoefficients(0f, 0f, 0f),
    val currentPeriod: Period? = null,
    val individualTimeTable: IndividualTimeTable? = null,
    val sheet: DashboardSheet = DashboardSheet.Closed,
    val selectedWeekday: Int = 2,
    val timetableLoading: Boolean = false,
    val timetableError: Error.DataError? = null,
    val coefficientsError: Error.DataError? = null,
    val classesError: Error.DataError? = null,
    val coefficientsLoading: Boolean = true,
    val classesLoading: Boolean = true
)
