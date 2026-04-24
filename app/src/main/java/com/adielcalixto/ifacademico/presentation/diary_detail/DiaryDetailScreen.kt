package com.adielcalixto.ifacademico.presentation.diary_detail

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                                style = MaterialTheme.typography.titleSmall,
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
                style = MaterialTheme.typography.titleSmall,
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
                style = MaterialTheme.typography.titleMedium,
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
                style = MaterialTheme.typography.titleSmall,
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

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        groupedExams.entries.forEach { entry ->
            StepExamCard(
                stepName = entry.key,
                exams = entry.value,
            )
        }
    }
}

@Composable
private fun StepExamCard(
    stepName: String,
    exams: List<Exam>,
) {
    var expanded by remember { mutableStateOf(false) }
    val averageExam = exams.find { it.acronym == "Média" }

    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stepName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (averageExam != null) {
                        Text(
                            text = "média: %.2f".format(averageExam.grade),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) "Recolher" else "Expandir",
                        modifier = Modifier
                            .padding(4.dp)
                            .height(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))

                exams.forEach { exam ->
                    if (exam.acronym != "Média") {
                        ExamRow(exam = exam)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                if (averageExam != null) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    AverageRow(exam = averageExam)
                }
            }
        }
    }
}

@Composable
private fun ExamRow(exam: Exam) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                tonalElevation = 1.dp,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(end = 6.dp)
            ) {
                Text(
                    text = exam.acronym,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Text(
                text = exam.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = "${exam.grade} / ${exam.maxGrade}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun AverageRow(exam: Exam) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    tonalElevation = 1.dp,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = exam.acronym,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = exam.formula ?: UiText.StringResource(R.string.formula_not_set).asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = "%.2f".format(exam.grade),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
