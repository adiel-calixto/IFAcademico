package com.adielcalixto.ifacademico.presentation.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.UiText
import com.adielcalixto.ifacademico.presentation.components.ErrorComponent
import com.adielcalixto.ifacademico.presentation.components.LoadingComponent
import com.adielcalixto.ifacademico.presentation.dashboard.components.PerformanceCoefficientCard

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        LoadingComponent()
        return
    }

    if (state.error != null) {
        ErrorComponent(
            state.error!!,
            onRetryClicked = { viewModel.onAction(DashboardAction.LoadViewModelData) })
        return
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
        // Performance coefficients
        Row(modifier = Modifier.height(96.dp)) {
            PerformanceCoefficientCard(
                label = UiText.StringResource(R.string.student_coefficient).asString(),
                value = state.performanceCoefficients.studentPerformance,
                Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.padding(8.dp))

            PerformanceCoefficientCard(
                label = UiText.StringResource(R.string.class_coefficient).asString(),
                value = state.performanceCoefficients.classPerformance,
                Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))

        // Time table
        Surface(
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    "${
                        UiText.StringResource(R.string.classes_of_the_day).asString()
                    } ${state.timeTable.today}",
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(8.dp))

                if (state.timeTable.classes.isEmpty()) {
                    Text(UiText.StringResource(R.string.empty_timetable_classes).asString())
                } else {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        state.timeTable.classes.forEach { timeTableClass ->
                            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚬")
                                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                    Column {
                                        Text(
                                            timeTableClass.className,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            timeTableClass.professorName,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Light
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(8.dp)
                                )

                                Row {
                                    Text(timeTableClass.classRoomName)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text("${timeTableClass.startTime} - ${timeTableClass.endTime}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}