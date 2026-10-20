package com.adielcalixto.ifacademico.presentation.diary_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.Diary
import com.adielcalixto.ifacademico.domain.entities.Exam
import com.adielcalixto.ifacademico.domain.entities.Period
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.asUiText
import com.adielcalixto.ifacademico.presentation.components.*
import com.adielcalixto.ifacademico.presentation.diary_detail.components.*
import com.adielcalixto.ifacademico.presentation.diary_list.SubjectAssessments
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

internal fun hasLowAllowance(absences: Int, maximum: Int): Boolean =
    maximum > 0 && absences.toLong() * 5 >= maximum.toLong() * 4

internal fun assessmentStages(exams: List<Exam>, locale: Locale, stageLabel: (String) -> String): List<IFAssessmentStage> {
    val number = NumberFormat.getNumberInstance(locale).apply { maximumFractionDigits = 2 }
    val date = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale)
    return exams.sortedBy { it.order }.groupBy { it.stepId }.map { (stage, records) ->
        IFAssessmentStage(stageLabel(stage), records.map {
            IFAssessmentRecord(
                it.description, it.acronym, it.date?.format(date), it.grade?.let(number::format),
                number.format(it.maxGrade), it.formula
            )
        })
    }
}

@Composable
internal fun DiaryDetailContent(
    diary: Diary,
    period: Period,
    assessments: SubjectAssessments,
    locale: Locale,
    onRetryAssessments: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(IFSpacing.lg)) {
        Text(
            stringResource(R.string.subject_context,
                diary.professor.ifBlank { stringResource(R.string.teacher_not_supplied) }, period.year, period.number),
            style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary
        )
        IFAttendanceSummary(IFAttendanceRecord(
            diary.absences, diary.maxAbsences, diary.remainingAbsences, diary.excusedAbsences,
            hasLowAllowance(diary.absences, diary.maxAbsences)
        ))
        when {
            assessments.isLoading -> AssessmentsLoading()
            assessments.error != null -> Column(verticalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
                IFSectionTitle(stringResource(R.string.exams), detail = true)
                IFErrorState(assessments.error.asUiText().asString(), onRetryAssessments)
            }
            else -> {
                IFAssessments(assessmentStages(assessments.exams, locale) { it }.map { stage ->
                    stage.copy(label = if (stage.label.isBlank()) stringResource(R.string.stage_not_supplied)
                        else stringResource(R.string.assessment_stage, stage.label))
                })
            }
        }
    }
}
