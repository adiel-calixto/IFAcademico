package com.adielcalixto.ifacademico.presentation.dashboard.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.PerformanceCoefficients
import com.adielcalixto.ifacademico.presentation.UiText

@Composable
internal fun PerformanceCoefficientSection(performanceCoefficients: PerformanceCoefficients) {
    Row(modifier = Modifier.height(96.dp)) {
        PerformanceCoefficientCard(
            label = UiText.StringResource(R.string.student_coefficient).asString(),
            value = performanceCoefficients.studentPerformance,
            Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.padding(8.dp))

        PerformanceCoefficientCard(
            label = UiText.StringResource(R.string.class_coefficient).asString(),
            value = performanceCoefficients.classPerformance,
            Modifier.weight(1f)
        )
    }
}