package com.adielcalixto.ifacademico.presentation.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R

@Composable
fun IFSectionTitle(title: String, modifier: Modifier = Modifier, detail: Boolean = false) {
    Text(title, modifier.semantics { heading() },
        style = if (detail) IFTheme.typography.detailTitle else IFTheme.typography.titleSm,
        color = IFTheme.colors.onSurface)
}

@Composable
fun IFCoefficientPanel(student: String, classValue: String, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxWidth(), color = IFTheme.colors.performanceSurface,
        contentColor = IFTheme.colors.onPerformance, shape = IFShapes.lg) {
        Column(Modifier.padding(IFSpacing.panel), verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
            Text(stringResource(R.string.student_coefficient), style = IFTheme.typography.labelSm,
                color = IFTheme.colors.performanceSecondary)
            Text(student, style = if (IFTheme.pageInset == IFSpacing.pageInsetNarrow)
                IFTheme.typography.coefficientNarrow else IFTheme.typography.coefficient)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
                Text(stringResource(R.string.class_coefficient), style = IFTheme.typography.bodySm,
                    color = IFTheme.colors.performanceSecondary)
                Text(classValue, style = IFTheme.typography.labelMd)
            }
        }
    }
}

data class IFClassRecord(
    val subject: String, val start: String, val end: String,
    val room: String?, val teacher: String?, val finished: Boolean = false,
    val period: String? = null,
)

@Composable
fun IFClassRow(record: IFClassRecord, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = IFSpacing.md), horizontalArrangement = Arrangement.spacedBy(IFSpacing.sm)) {
            Column(Modifier.widthIn(max = 76.dp), verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
                Text(record.start, style = IFTheme.typography.labelMd, color = IFTheme.colors.onSurface)
                Text(record.end, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
            }
            Box(Modifier.width(2.dp).height(48.dp).background(IFTheme.colors.primary))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
                Text(record.subject, style = IFTheme.typography.titleMd, color = IFTheme.colors.onSurface)
                record.room?.let { Text(it, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary) }
                record.teacher?.let { Text(it, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary) }
                if (record.finished) Text(stringResource(R.string.class_finished),
                    style = IFTheme.typography.labelSm, color = IFTheme.colors.secondary)
            }
            Icon(IFIcon.Chevron.vector, null, Modifier.size(IFSize.icon), tint = IFTheme.colors.secondary)
        }
        HorizontalDivider(color = IFTheme.colors.outline)
    }
}

@Composable
fun IFClassDetail(record: IFClassRecord, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
        Text(record.subject, style = IFTheme.typography.titleMd)
        IFRecord(stringResource(R.string.class_time), "${record.start} – ${record.end}")
        record.room?.let { IFRecord(stringResource(R.string.room), it) }
        record.teacher?.let { IFRecord(stringResource(R.string.professor), it) }
        record.period?.let { IFRecord(stringResource(R.string.period), it) }
        IFRecord(stringResource(R.string.class_status), stringResource(
            if (record.finished) R.string.class_finished else R.string.class_not_finished))
    }
}

@Composable
fun IFWeekdaySelector(days: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
        days.forEachIndexed { index, label ->
            val interaction = remember { MutableInteractionSource() }
            val focused by interaction.collectIsFocusedAsState()
            Surface(shape = IFShapes.sm, color = if (selected == index) IFTheme.colors.primaryContainer else IFTheme.colors.surface,
                contentColor = if (selected == index) IFTheme.colors.primary else IFTheme.colors.secondary) {
                Box(Modifier
                    .then(if (focused) Modifier.border(2.dp, IFTheme.colors.primary, IFShapes.sm) else Modifier)
                    .selectable(selected == index, role = Role.Tab, interactionSource = interaction,
                        indication = ripple(), onClick = { onSelect(index) })
                    .sizeIn(minWidth = IFSize.touchTarget, minHeight = IFSize.touchTarget)
                    .padding(IFSpacing.xs), contentAlignment = Alignment.Center) {
                    Text(label, style = IFTheme.typography.labelMd)
                }
            }
        }
    }
}

@Composable
fun IFTimetableContent(
    period: String, weekdays: List<String>, selectedDay: Int, classes: List<IFClassRecord>,
    onDaySelect: (Int) -> Unit, onClassClick: (IFClassRecord) -> Unit, modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
        Text(period, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
        IFWeekdaySelector(weekdays, selectedDay, onDaySelect)
        if (classes.isEmpty()) IFEmptyState(stringResource(R.string.empty_timetable_classes))
        classes.forEach { record -> IFClassRow(record, { onClassClick(record) }) }
    }
}

data class IFSubjectRecord(val name: String, val teacher: String, val assessmentCount: Int, val hours: Int)

@Composable
fun IFSubjectRow(subject: IFSubjectRecord, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = IFSpacing.md), horizontalArrangement = Arrangement.spacedBy(IFSpacing.sm)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
                Text(subject.name, style = IFTheme.typography.titleMd, color = IFTheme.colors.onSurface)
                Text(subject.teacher, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
                Text(stringResource(R.string.subject_metadata, subject.assessmentCount, subject.hours),
                    style = IFTheme.typography.labelSm, color = IFTheme.colors.secondary)
            }
            Icon(IFIcon.Chevron.vector, null, Modifier.size(IFSize.icon), tint = IFTheme.colors.secondary)
        }
        HorizontalDivider(color = IFTheme.colors.outline)
    }
}

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
            LinearProgressIndicator(
                progress = { if (record.maximum > 0) (record.absences.toFloat() / record.maximum).coerceIn(0f, 1f) else 0f },
                modifier = Modifier.fillMaxWidth(), color = IFTheme.colors.primary, trackColor = IFTheme.colors.outline,
            )
            Text(stringResource(R.string.absence_usage, record.absences, record.maximum), style = IFTheme.typography.bodySm)
            if (record.lowAllowance) IFNotice(stringResource(R.string.low_attendance_allowance))
        }
    }
}

data class IFClassProgressRecord(val hours: Int, val planned: Int, val taught: Int, val pending: Int)

@Composable
fun IFClassProgress(record: IFClassProgressRecord, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(IFSpacing.sm)) {
        IFSectionTitle(stringResource(R.string.class_progress), detail = true)
        IFRecord(stringResource(R.string.total_hours), stringResource(R.string.hours_value, record.hours))
        IFRecord(stringResource(R.string.planned_classes), record.planned.toString())
        IFRecord(stringResource(R.string.taught_classes), record.taught.toString())
        IFRecord(stringResource(R.string.pending_classes), record.pending.toString())
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
            Column(Modifier.weight(1f)) {
                Text(record.grade ?: stringResource(R.string.not_graded),
                    style = IFTheme.typography.labelMd, color = IFTheme.colors.onSurface)
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

@Composable
fun IFEventPanel(
    type: String, description: String, date: String, modifier: Modifier = Modifier,
    subject: String? = null, stage: String? = null, teacher: String? = null, room: String? = null,
) {
    Surface(modifier.fillMaxWidth(), color = IFTheme.colors.surface, shape = IFShapes.md,
        border = BorderStroke(1.dp, IFTheme.colors.outline)) {
        Column(Modifier.padding(IFSpacing.md), verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
            Text(type, style = IFTheme.typography.labelSm, color = IFTheme.colors.secondary)
            Text(description, style = IFTheme.typography.detailTitle, color = IFTheme.colors.onSurface)
            subject?.let { Text(it, style = IFTheme.typography.bodyMd) }
            stage?.let { Text(it, style = IFTheme.typography.labelSm, color = IFTheme.colors.secondary) }
            Text(date, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
            teacher?.let { Text(it, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary) }
            room?.let { Text(it, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary) }
        }
    }
}
