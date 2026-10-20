package com.adielcalixto.ifacademico.presentation.diary_detail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.IFEmptyState
import com.adielcalixto.ifacademico.presentation.components.IFIcon
import com.adielcalixto.ifacademico.presentation.components.IFRecord
import com.adielcalixto.ifacademico.presentation.components.IFSectionTitle
import java.text.NumberFormat

data class IFAttendanceRecord(
    val absences: Int,
    val maximum: Int,
    val excused: Int,
    val lowAllowance: Boolean,
    val plannedClasses: Int,
    val taughtClasses: Int,
    val pendingClasses: Int,
)

@Composable
fun IFAttendanceSummary(record: IFAttendanceRecord, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxWidth(), shape = IFShapes.md, color = IFTheme.colors.surfaceSubtle) {
        Column(Modifier.padding(IFSpacing.md), verticalArrangement = Arrangement.spacedBy(IFSpacing.sm)) {
            IFSectionTitle(
                stringResource(R.string.attendance), Modifier.fillMaxWidth(),
                detail = true, textAlign = TextAlign.Center
            )
            IFRecord(stringResource(R.string.absences), record.absences.toString())
            IFRecord(stringResource(R.string.max_absences), record.maximum.toString())
            IFRecord(stringResource(R.string.excused_absences), record.excused.toString())
            ClassProgress(record)
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

@Composable
private fun ClassProgress(record: IFAttendanceRecord) {
    val number = NumberFormat.getIntegerInstance(LocalConfiguration.current.locales[0])
    val taughtLabel = stringResource(R.string.taught_classes)
    val pendingLabel = stringResource(R.string.pending_classes)
    val taughtCount = number.format(record.taughtClasses)
    val pendingCount = number.format(record.pendingClasses)
    val plannedCount = number.format(record.plannedClasses)
    val completedColor = IFTheme.colors.primary
    val pendingColor = IFTheme.colors.secondary
    val trackColor = IFTheme.colors.outline
    val description = stringResource(
        R.string.class_progress_description, taughtCount, pendingCount, plannedCount
    )

    Column(verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
        if (record.plannedClasses > 0) {
            val taught = record.taughtClasses.coerceIn(0, record.plannedClasses)
            val pending = record.pendingClasses.coerceIn(0, record.plannedClasses - taught)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(number.format(0), style = IFTheme.typography.labelSm, color = IFTheme.colors.secondary)
                Text(plannedCount, style = IFTheme.typography.labelSm, color = IFTheme.colors.secondary)
            }
            Canvas(
                Modifier.fillMaxWidth().height(6.dp).clip(IFShapes.full).semantics {
                    contentDescription = description
                    progressBarRangeInfo = ProgressBarRangeInfo(
                        taught.toFloat(), 0f..record.plannedClasses.toFloat()
                    )
                }
            ) {
                drawRect(trackColor)
                val taughtWidth = size.width * (taught.toFloat() / record.plannedClasses)
                val pendingWidth = size.width * (pending.toFloat() / record.plannedClasses)
                drawRect(completedColor, size = Size(taughtWidth, size.height))
                drawRect(pendingColor, topLeft = Offset(taughtWidth, 0f), size = Size(pendingWidth, size.height))
            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(IFSpacing.md),
            verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)
        ) {
            ClassProgressLabel(completedColor, taughtLabel, taughtCount)
            ClassProgressLabel(pendingColor, pendingLabel, pendingCount)
        }
    }
}

@Composable
private fun ClassProgressLabel(color: Color, label: String, count: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(IFSpacing.xs), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(IFSpacing.xs).clip(IFShapes.full).background(color))
        Text(
            stringResource(R.string.class_progress_count, label, count),
            style = IFTheme.typography.bodySm,
            color = IFTheme.colors.onSurface
        )
    }
}

data class IFAssessmentRecord(
    val description: String, val acronym: String, val date: String?,
    val grade: String?, val maximum: String, val formula: String? = null,
)
data class IFAssessmentStage(val label: String, val assessments: List<IFAssessmentRecord>)

@Composable
fun IFAssessmentRow(record: IFAssessmentRecord, modifier: Modifier = Modifier) {
    var expanded by remember(record) { mutableStateOf(false) }
    AssessmentRowContent(record, expanded, { expanded = !expanded }, modifier)
}

@Composable
internal fun AssessmentRowContent(
    record: IFAssessmentRecord,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val expansionState = stringResource(
        if (expanded) R.string.assessment_expanded else R.string.assessment_collapsed
    )
    val action = stringResource(
        if (expanded) R.string.collapse_assessment else R.string.expand_assessment,
        record.description
    )
    Column(modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth()
                .semantics { stateDescription = expansionState }
                .clickable(role = Role.Button, onClickLabel = action, onClick = onToggle)
                .heightIn(min = 48.dp)
                .padding(vertical = IFSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(IFSpacing.sm)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(IFSpacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BoxWithConstraints(Modifier.weight(1f)) {
                    // Reserve room for the chevron before deciding whether two columns fit.
                    val stacked = maxWidth < 280.dp * LocalDensity.current.fontScale
                    val scoreMaxWidth = maxWidth * .4f
                    if (stacked) {
                        Column(verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
                            AssessmentName(record)
                            AssessmentScore(record, stacked = true)
                        }
                    } else {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(IFSpacing.md)
                        ) {
                            AssessmentName(record, Modifier.weight(1f))
                            AssessmentScore(record, Modifier.widthIn(max = scoreMaxWidth))
                        }
                    }
                }
                Icon(
                    IFIcon.Down.vector, null,
                    Modifier.size(IFSize.icon).rotate(if (expanded) 180f else 0f),
                    tint = IFTheme.colors.secondary
                )
            }
            if (expanded) AssessmentDetails(record)
        }
    }
}

@Composable
private fun AssessmentName(record: IFAssessmentRecord, modifier: Modifier = Modifier) {
    Text(record.description, modifier, style = IFTheme.typography.bodySm, color = IFTheme.colors.onSurface)
}

@Composable
private fun AssessmentScore(
    record: IFAssessmentRecord,
    modifier: Modifier = Modifier,
    stacked: Boolean = false,
) {
    Text(
        record.grade?.let { stringResource(R.string.assessment_score, it) }
            ?: stringResource(R.string.not_graded),
        modifier,
        style = IFTheme.typography.bodySm.copy(
            fontWeight = if (record.grade != null) FontWeight.SemiBold else FontWeight.Normal
        ),
        color = IFTheme.colors.onSurface,
        textAlign = if (stacked) TextAlign.Start else TextAlign.End
    )
}

@Composable
private fun AssessmentDetails(record: IFAssessmentRecord) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)
    ) {
        Text(
            stringResource(R.string.maximum_score, record.maximum),
            style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary, textAlign = TextAlign.End
        )
        record.acronym.takeIf { it.isNotBlank() }?.let {
            Text(stringResource(R.string.assessment_acronym, it),
                style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary, textAlign = TextAlign.End)
        }
        Text(
            stringResource(R.string.assessment_date, record.date ?: stringResource(R.string.date_not_supplied)),
            style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary, textAlign = TextAlign.End
        )
        record.formula?.takeIf { it.isNotBlank() }?.let {
            Surface(color = IFTheme.colors.surfaceSubtle, shape = IFShapes.sm) {
                Column(
                    Modifier.fillMaxWidth().padding(IFSpacing.sm),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(stringResource(R.string.formula), style = IFTheme.typography.labelSm, textAlign = TextAlign.End)
                    Text(it, style = IFTheme.typography.bodySm, textAlign = TextAlign.End)
                }
            }
        }
    }
}

@Composable
fun IFAssessments(stages: List<IFAssessmentStage>, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
        IFSectionTitle(stringResource(R.string.exams), Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        if (stages.all { it.assessments.isEmpty() }) IFEmptyState(stringResource(R.string.no_exams))
        stages.forEachIndexed { stageIndex, stage ->
            if (stageIndex > 0) HorizontalDivider(color = IFTheme.colors.outline)
            Column {
                Text(stage.label, Modifier.padding(bottom = IFSpacing.xs),
                    style = IFTheme.typography.labelMd, color = IFTheme.colors.secondary)
                stage.assessments.forEachIndexed { assessmentIndex, assessment ->
                    if (assessmentIndex > 0) {
                        HorizontalDivider(color = IFTheme.colors.outline.copy(alpha = .4f))
                    }
                    IFAssessmentRow(assessment)
                }
            }
        }
    }
}
