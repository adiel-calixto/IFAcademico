package com.adielcalixto.ifacademico.presentation.dashboard

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.entities.PerformanceCoefficients
import com.adielcalixto.ifacademico.domain.entities.TimeTable

data class DashboardState(
    val timeTable: TimeTable,
    val performanceCoefficients: PerformanceCoefficients,
    val error: Error.DataError? = null,
    val isLoading: Boolean = false
)
