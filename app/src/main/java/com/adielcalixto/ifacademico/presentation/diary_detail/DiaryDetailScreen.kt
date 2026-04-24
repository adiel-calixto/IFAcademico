package com.adielcalixto.ifacademico.presentation.diary_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.Exam
import com.adielcalixto.ifacademico.presentation.UiText
import com.adielcalixto.ifacademico.presentation.components.ErrorComponent
import com.adielcalixto.ifacademico.presentation.components.LoadingComponent
import com.adielcalixto.ifacademico.presentation.diary_list.components.ClassesSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(
    viewModel: DiaryDetailViewModel,
    navController: NavController,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.diary?.name ?: UiText.StringResource(R.string.diary)
                                .asString(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                        )
                        if (state.diary != null) {
                            Text(
                                text = state.diary!!.professor,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingComponent()
            state.error != null -> ErrorComponent(state.error!!) {
                viewModel.onAction(DiaryDetailAction.LoadData)
            }

            state.diary != null -> {
                val diary = state.diary!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    AbsencesCard(
                        absences = diary.absences,
                        maxAbsences = diary.maxAbsences,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ClassesCard(
                        plannedClasses = diary.plannedClasses,
                        taughtClasses = diary.taughtClasses,
                        pendingClasses = diary.pendingClasses
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Surface(
                        tonalElevation = 1.dp,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = UiText.StringResource(R.string.exams).asString(),
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            ExamsList(exams = state.exams)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AbsencesCard(absences: Int, maxAbsences: Int) {
    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = UiText.StringResource(R.string.absences).asString(),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    value = absences.toString(),
                    label = UiText.StringResource(R.string.absences).asString(),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    value = maxAbsences.toString(),
                    label = UiText.StringResource(R.string.max_absences).asString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ClassesCard(
    plannedClasses: Int,
    taughtClasses: Int,
    pendingClasses: Int
) {
    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = UiText.StringResource(R.string.classes_summary).asString(),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ClassesSummary(
                plannedClasses = plannedClasses,
                taughtClasses = taughtClasses,
                pendingClasses = pendingClasses
            )
        }
    }
}

@Composable
private fun ExamsList(exams: List<Exam>) {
    if (exams.isEmpty()) {
        Text(
            text = UiText.StringResource(R.string.no_exams).asString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val groupedExams = exams.groupBy { it.stepId }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        groupedExams.entries.withIndex().forEach { (index, entry) ->
            entry.value.forEach { exam ->
                CompactExamRow(exam = exam)
            }

            if (index < groupedExams.entries.size - 1) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
            }
        }
    }
}

@Composable
private fun CompactExamRow(exam: Exam) {
    val isAverage = exam.acronym == "Média"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (isAverage) {
                Surface(
                    tonalElevation = 1.dp,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = exam.acronym,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else {
                Text(
                    text = exam.acronym,
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp),
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = " - ${exam.description}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = if (isAverage) {
                "%.2f".format(exam.grade)
            } else {
                "${exam.grade} / ${exam.maxGrade}"
            },
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp),
            fontWeight = if (isAverage) FontWeight.Bold else FontWeight.Normal,
            color = if (isAverage) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    }
}
