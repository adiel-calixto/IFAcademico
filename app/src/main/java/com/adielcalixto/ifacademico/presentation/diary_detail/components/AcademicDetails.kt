package com.adielcalixto.ifacademico.presentation.diary_detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.IFEmptyState
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
            if (record.lowAllowance) {
                Surface(
                    modifier = Modifier.align(Alignment.End),
                    color = IFTheme.colors.notice,
                    contentColor = IFTheme.colors.onNotice,
                    shape = IFShapes.sm
                ) {
                    Text(
                        stringResource(R.string.low_attendance_allowance),
                        Modifier.padding(horizontal = IFSpacing.xs, vertical = IFSpacing.micro),
                        style = IFTheme.typography.labelSm,
                        textAlign = TextAlign.End
                    )
                }
            }
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
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            // Keep descriptions readable instead of squeezing two columns at larger font scales.
            val stacked = maxWidth < 280.dp * LocalDensity.current.fontScale
            if (stacked) {
                Column(verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
                    AssessmentIdentity(record)
                    AssessmentScore(record, stacked = true)
                }
            } else {
                val scoreMaxWidth = maxWidth * .4f
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(IFSpacing.md)
                ) {
                    AssessmentIdentity(record, Modifier.weight(1f))
                    AssessmentScore(record, Modifier.widthIn(max = scoreMaxWidth))
                }
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
private fun AssessmentIdentity(record: IFAssessmentRecord, modifier: Modifier = Modifier) {
    val date = record.date ?: stringResource(R.string.date_not_supplied)
    val metadata = if (record.acronym.isBlank()) {
        date
    } else {
        stringResource(R.string.assessment_metadata, record.acronym, date)
    }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
        Text(record.description, style = IFTheme.typography.bodyMd, color = IFTheme.colors.onSurface)
        Text(metadata, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
    }
}

@Composable
private fun AssessmentScore(
    record: IFAssessmentRecord,
    modifier: Modifier = Modifier,
    stacked: Boolean = false,
) {
    Column(modifier, horizontalAlignment = if (stacked) Alignment.Start else Alignment.End) {
        val textAlign = if (stacked) TextAlign.Start else TextAlign.End
        if (record.grade != null) {
            Text(
                stringResource(R.string.assessment_score, record.grade),
                style = IFTheme.typography.bodyMd.copy(fontWeight = FontWeight.SemiBold),
                color = IFTheme.colors.onSurface,
                textAlign = textAlign
            )
        } else {
            Text(stringResource(R.string.not_graded), style = IFTheme.typography.bodySm,
                color = IFTheme.colors.onSurface, textAlign = textAlign)
        }
        Text(stringResource(R.string.maximum_score, record.maximum),
            style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary, textAlign = textAlign)
    }
}

@Composable
fun IFAssessments(stages: List<IFAssessmentStage>, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
        IFSectionTitle(stringResource(R.string.exams), detail = true)
        if (stages.all { it.assessments.isEmpty() }) IFEmptyState(stringResource(R.string.no_exams))
        stages.forEach { stage ->
            Column {
                Text(stage.label, Modifier.padding(bottom = IFSpacing.xs),
                    style = IFTheme.typography.labelMd, color = IFTheme.colors.secondary)
                stage.assessments.forEach { IFAssessmentRow(it) }
            }
        }
    }
}
