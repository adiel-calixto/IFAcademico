package com.adielcalixto.ifacademico.presentation.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.UiText
import com.adielcalixto.ifacademico.presentation.components.ErrorComponent
import com.adielcalixto.ifacademico.presentation.components.LoadingComponent
import com.adielcalixto.ifacademico.presentation.dashboard.components.IndividualTimeTableSection
import com.adielcalixto.ifacademico.presentation.dashboard.components.PerformanceCoefficientSection
import com.adielcalixto.ifacademico.presentation.dashboard.components.TodayTimeTable

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val state by viewModel.state.collectAsState()
    var showIndividualTimeTable by remember { mutableStateOf(false) }

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

    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
        item {
            PerformanceCoefficientSection(state.performanceCoefficients)
            Spacer(modifier = Modifier.padding(8.dp))
        }

        item {
            AnimatedContent(
                targetState = showIndividualTimeTable,
                transitionSpec = {
                    if (targetState) {
                        slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) togetherWith
                                slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth })
                    } else {
                        slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) togetherWith
                                slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                    }
                },
                label = "TimeTableTransition",
                modifier = Modifier.fillMaxWidth()
            ) { showIndividual ->
                if (!showIndividual) {
                    TodayTimeTable(state.timeTable)
                } else {
                    if (state.individualTimeTable == null) {
                        LinearProgressIndicator()
                    } else {
                        IndividualTimeTableSection(state.individualTimeTable!!)
                    }
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        showIndividualTimeTable = !showIndividualTimeTable
                        if (showIndividualTimeTable) {
                            viewModel.onAction(DashboardAction.ShowIndividualTimeTable)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (showIndividualTimeTable) Icons.Filled.Schedule else Icons.Filled.CalendarMonth,
                        contentDescription = "Schedule Icon"
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        UiText.StringResource(
                            if (showIndividualTimeTable) R.string.classes_of_the_day
                            else R.string.individual_time_table
                        ).asString()
                    )
                }
            }
        }
    }
}