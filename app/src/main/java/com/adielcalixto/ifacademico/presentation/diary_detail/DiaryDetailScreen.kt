package com.adielcalixto.ifacademico.presentation.diary_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.Exam
import com.adielcalixto.ifacademico.presentation.UiText
import com.adielcalixto.ifacademico.presentation.components.ErrorComponent
import com.adielcalixto.ifacademico.presentation.components.LoadingComponent
import com.adielcalixto.ifacademico.presentation.diary_list.components.ClassesSummary
import java.time.format.DateTimeFormatter

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

                            ExamsTimeline(exams = state.exams)
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
private fun ExamsTimeline(exams: List<Exam>) {
    if (exams.isEmpty()) {
        Text(
            text = UiText.StringResource(R.string.no_exams).asString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val groupedExams = exams.groupBy { it.stepId }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        groupedExams.entries.forEachIndexed { index, entry ->
            val isLast = index == groupedExams.entries.size - 1
            val nonAverageExams = entry.value.filter { it.acronym != "Média" }

            BimestreSection(
                stepId = entry.key,
                exams = nonAverageExams,
                dateFormatter = dateFormatter
            )

            if (!isLast) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AveragesSection(exams = exams)
    }
}

@Composable
private fun BimestreSection(
    stepId: String,
    exams: List<Exam>,
    dateFormatter: DateTimeFormatter
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = stepId,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        exams.forEachIndexed { index, exam ->
            val isLast = index < exams.size - 1
            ExamTimelineItem(
                exam = exam,
                dateFormatter = dateFormatter,
                showLine = isLast
            )
            if (isLast) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ExamTimelineItem(
    exam: Exam,
    dateFormatter: DateTimeFormatter,
    showLine: Boolean
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            TimelineDot(hasGrade = exam.hasGrade)
            if (showLine) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = exam.acronym,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = exam.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = (exam.date?.format(dateFormatter)?.let { "$it · " } ?: "") + "max ${exam.maxGrade}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (exam.hasGrade) {
                    Text(
                        text = "%.1f".format(exam.grade),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "—",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = UiText.StringResource(R.string.no_grade).asString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineDot(hasGrade: Boolean) {
    if (hasGrade) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
    } else {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), CircleShape)
            )
        }
    }
}

@Composable
private fun AveragesSection(exams: List<Exam>) {
    val averageExams = exams.filter { it.acronym == "Média" }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = UiText.StringResource(R.string.calculated_averages).asString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            averageExams.forEach { avg ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = avg.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (avg.hasGrade) {
                                "%.2f".format(avg.grade)
                            } else {
                                UiText.StringResource(R.string.not_configured).asString()
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (avg.hasGrade) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
