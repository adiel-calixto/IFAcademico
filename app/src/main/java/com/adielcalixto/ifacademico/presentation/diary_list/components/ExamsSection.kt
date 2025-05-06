package com.adielcalixto.ifacademico.presentation.diary_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.Exam
import com.adielcalixto.ifacademico.presentation.UiText

private fun Exam.isOfTypeAverage() = acronym == "Média"

@Composable
internal fun ExamsSection(exams: List<Exam>) {
    Column {
        Text(
            UiText.StringResource(R.string.exams).asString(),
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.bodySmall.fontSize
        )
        Spacer(modifier = Modifier.height(8.dp))

        val groupedExams = exams.groupBy { it.stepId }

        groupedExams.entries.withIndex().forEach { (index, entry) ->
            entry.value.forEach { exam ->
                if (exam.isOfTypeAverage()) AverageExam(exam) else NormalExam(exam)
            }

            if (index < groupedExams.entries.size - 1)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }
    }
}

@Composable
private fun AverageExam(exam: Exam) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
            ExamDescription(exam)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${UiText.StringResource(R.string.grade).asString()}:",
                        modifier = Modifier.padding(2.dp),
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                    Text(
                        text = exam.grade.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = exam.formula
                        ?: UiText.StringResource(R.string.formula_not_set)
                            .asString(),
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
            }
        }
    }
}

@Composable
private fun NormalExam(exam: Exam) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
            ExamDescription(exam)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${UiText.StringResource(R.string.grade).asString()}:",
                        modifier = Modifier.padding(2.dp),
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                    Text(
                        text = "${exam.grade} / ${exam.maxGrade}",
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                }
            }
        }
    }
}

@Composable
private fun ExamDescription(exam: Exam) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = exam.acronym,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text("-", modifier = Modifier.padding(horizontal = 2.dp))
        Text(
            text = exam.description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}