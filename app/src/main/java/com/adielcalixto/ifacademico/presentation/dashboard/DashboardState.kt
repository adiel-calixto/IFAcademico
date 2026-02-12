package com.adielcalixto.ifacademico.presentation.dashboard

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.entities.IndividualTimeTable
import com.adielcalixto.ifacademico.domain.entities.PerformanceCoefficients
import com.adielcalixto.ifacademico.domain.entities.Period
import com.adielcalixto.ifacademico.domain.entities.TimeTable

data class DashboardState(
    val timeTable: TimeTable,
    val performanceCoefficients: PerformanceCoefficients,
    val actualPeriod: Period? = null,
    val individualTimeTable: IndividualTimeTable? = null,
    val showIndividualTimeTable: Boolean = false,
    val error: Error.DataError? = null,
    val isLoading: Boolean = false
)
