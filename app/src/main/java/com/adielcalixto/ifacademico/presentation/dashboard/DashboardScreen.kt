package com.adielcalixto.ifacademico.presentation.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.presentation.components.ErrorComponent
import com.adielcalixto.ifacademico.presentation.components.LoadingComponent
import com.adielcalixto.ifacademico.presentation.dashboard.components.IndividualTimeTableSection
import com.adielcalixto.ifacademico.presentation.dashboard.components.PerformanceCoefficientSection
import com.adielcalixto.ifacademico.presentation.dashboard.components.TodayTimeTable

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        AnimatedContent(
            targetState = state.showIndividualTimeTable,
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
                Column {
                    TodayTimeTable(
                        timeTable = state.timeTable,
                        onOpenIndividualTimeTableClicked = { viewModel.onAction(DashboardAction.ShowIndividualTimeTable) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PerformanceCoefficientSection(
                        performanceCoefficients = state.performanceCoefficients
                    )
                }
            } else {
                IndividualTimeTableSection(state.individualTimeTable!!) {
                    viewModel.onAction(DashboardAction.HideIndividualTimeTable)
                }
            }
        }
    }
}
