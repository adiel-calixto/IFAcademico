package com.adielcalixto.ifacademico.presentation.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.PerformanceCoefficients
import com.adielcalixto.ifacademico.presentation.UiText

@Composable
internal fun PerformanceCoefficientSection(
    performanceCoefficients: PerformanceCoefficients,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = UiText.StringResource(R.string.coefficients).asString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PerformanceCoefficientCard(
                    label = UiText.StringResource(R.string.student_coefficient).asString(),
                    value = performanceCoefficients.studentPerformance,
                    modifier = Modifier.weight(1f)
                )

                PerformanceCoefficientCard(
                    label = UiText.StringResource(R.string.class_coefficient).asString(),
                    value = performanceCoefficients.classPerformance,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
