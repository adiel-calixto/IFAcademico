package com.adielcalixto.ifacademico.presentation.diary_detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.IFEmptyState
import com.adielcalixto.ifacademico.presentation.components.IFNotice
import com.adielcalixto.ifacademico.presentation.components.IFRecord
import com.adielcalixto.ifacademico.presentation.components.IFSectionTitle

data class IFAttendanceRecord(
    val absences: Int, val maximum: Int, val remaining: Int, val excused: Int, val lowAllowance: Boolean,
)

@Composable
fun IFAttendanceSummary(record: IFAttendanceRecord, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxWidth(), shape = IFShapes.md, color = IFTheme.colors.surfaceSubtle) {
        Column(Modifier.padding(IFSpacing.md), verticalArrangement = Arrangement.spacedBy(IFSpacing.sm)) {
            IFSectionTitle(stringResource(R.string.attendance), detail = true)
            IFRecord(stringResource(R.string.absences), record.absences.toString())
            IFRecord(stringResource(R.string.max_absences), record.maximum.toString())
            IFRecord(stringResource(R.string.remaining_absences), record.remaining.toString())
            IFRecord(stringResource(R.string.excused_absences), record.excused.toString())
            if (record.maximum > 0) {
                val usage = record.absences.toFloat() / record.maximum
                LinearProgressIndicator(
                    progress = { usage.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(), color = IFTheme.colors.primary,
                    trackColor = IFTheme.colors.outline,
                )
                Text(stringResource(R.string.absence_usage_percent, kotlin.math.round(usage * 100).toInt()),
                    style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
            }
            if (record.lowAllowance) IFNotice(stringResource(R.string.low_attendance_allowance))
        }
    }
}

data class IFAssessmentRecord(
    val description: String, val acronym: String, val date: String?,
    val grade: String?, val maximum: String, val formula: String? = null,
)
data class IFAssessmentStage(val label: String, val assessments: List<IFAssessmentRecord>)

@Composable
fun IFAssessmentRow(record: IFAssessmentRecord, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(vertical = IFSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(IFSpacing.md),
            verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
                Text(record.description, style = IFTheme.typography.bodyMd, color = IFTheme.colors.onSurface)
                Text(record.acronym, style = IFTheme.typography.labelSm, color = IFTheme.colors.secondary)
                Text(record.date ?: stringResource(R.string.date_not_supplied),
                    style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(record.grade ?: stringResource(R.string.not_graded),
                    style = if (record.grade == null) IFTheme.typography.bodySm else IFTheme.typography.titleMd,
                    color = IFTheme.colors.onSurface)
                Text(stringResource(R.string.maximum_score, record.maximum),
                    style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
            }
        }
        record.formula?.takeIf { it.isNotBlank() }?.let {
            Surface(color = IFTheme.colors.surfaceSubtle, shape = IFShapes.sm) {
                Column(Modifier.fillMaxWidth().padding(IFSpacing.sm)) {
                    Text(stringResource(R.string.formula), style = IFTheme.typography.labelSm)
                    Text(it, style = IFTheme.typography.bodySm)
                }
            }
        }
        HorizontalDivider(color = IFTheme.colors.outline)
    }
}

@Composable
fun IFAssessments(stages: List<IFAssessmentStage>, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
        IFSectionTitle(stringResource(R.string.exams), detail = true)
        if (stages.all { it.assessments.isEmpty() }) IFEmptyState(stringResource(R.string.no_exams))
        stages.forEach { stage ->
            Text(stage.label, style = IFTheme.typography.labelMd, color = IFTheme.colors.secondary)
            stage.assessments.forEach { IFAssessmentRow(it) }
        }
    }
}
