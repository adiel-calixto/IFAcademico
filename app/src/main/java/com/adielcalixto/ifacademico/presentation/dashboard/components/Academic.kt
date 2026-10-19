package com.adielcalixto.ifacademico.presentation.dashboard.components

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
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.IFEmptyState
import com.adielcalixto.ifacademico.presentation.components.IFIcon
import com.adielcalixto.ifacademico.presentation.components.IFRecord

@Composable
fun IFCoefficientPanel(student: String, classValue: String, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxWidth(), color = IFTheme.colors.performanceSurface,
        contentColor = IFTheme.colors.onPerformance, shape = IFShapes.lg) {
        Column(Modifier.padding(IFSpacing.panel), verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
            Text(stringResource(R.string.student_coefficient), style = IFTheme.typography.labelSm,
                color = IFTheme.colors.performanceSecondary)
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(IFSpacing.sm),
                itemVerticalAlignment = Alignment.CenterVertically
            ) {
                Text(student, style = if (IFTheme.pageInset == IFSpacing.pageInsetNarrow)
                    IFTheme.typography.coefficientNarrow else IFTheme.typography.coefficient)
                Column(horizontalAlignment = Alignment.End) {
                    Text(stringResource(R.string.class_coefficient), style = IFTheme.typography.labelSm,
                        color = IFTheme.colors.performanceSecondary)
                    Text(classValue, style = IFTheme.typography.coefficientComparison,
                        color = IFTheme.colors.performanceSecondary)
                }
            }
        }
    }
}

data class IFClassRecord(
    val subject: String, val start: String, val end: String,
    val room: String?, val teacher: String?, val finished: Boolean = false,
    val period: String? = null,
    val weekday: String? = null,
)

@Composable
fun IFClassRow(record: IFClassRecord, onClick: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(vertical = IFSpacing.md), horizontalArrangement = Arrangement.spacedBy(IFSpacing.sm)) {
            Column(Modifier.widthIn(max = 76.dp), verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
                Text(record.start, style = IFTheme.typography.labelMd, color = IFTheme.colors.onSurface)
                Text(record.end, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
            }
            Box(Modifier.width(2.dp).height(48.dp).background(IFTheme.colors.primary))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
                Text(record.subject, style = IFTheme.typography.detailTitle, color = IFTheme.colors.onSurface)
                record.room?.let { Text(it, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary) }
                record.teacher?.let { Text(it, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary) }
                if (record.finished) Text(stringResource(R.string.class_finished),
                    style = IFTheme.typography.labelSm, color = IFTheme.colors.secondary)
            }
            if (onClick != null) Icon(IFIcon.Chevron.vector, null, Modifier.size(IFSize.icon), tint = IFTheme.colors.secondary)
        }
        HorizontalDivider(color = IFTheme.colors.outline)
    }
}

@Composable
fun IFClassDetail(record: IFClassRecord, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
        record.weekday?.let { IFRecord(stringResource(R.string.weekday), it) }
        IFRecord(stringResource(R.string.starts_at), record.start)
        IFRecord(stringResource(R.string.ends_at), record.end)
        record.room?.let { IFRecord(stringResource(R.string.room), it) }
        record.teacher?.let { IFRecord(stringResource(R.string.professor), it) }
        record.period?.let { IFRecord(stringResource(R.string.period), it) }
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
    onDaySelect: (Int) -> Unit, modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
        Text(period, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
        IFWeekdaySelector(weekdays, selectedDay, onDaySelect)
        if (classes.isEmpty()) IFEmptyState(stringResource(R.string.empty_timetable_classes))
        classes.forEach { record -> IFClassRow(record) }
    }
}
