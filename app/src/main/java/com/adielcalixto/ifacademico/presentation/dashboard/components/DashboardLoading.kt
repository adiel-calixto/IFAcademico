package com.adielcalixto.ifacademico.presentation.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.*

@Composable
internal fun DashboardLoading(modifier: Modifier = Modifier) {
    IFLoadingRegion(
        stringResource(R.string.loading_home),
        modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = IFTheme.pageInset, vertical = IFSpacing.lg),
    ) {
        IFSkeletonLine(.45f, style = IFTheme.typography.labelSm)
        Spacer(Modifier.height(IFSpacing.lg))
        Surface(
            color = IFTheme.colors.performanceSurface,
            shape = IFTheme.shapes.lg,
        ) {
            val placeholder = IFTheme.colors.performanceSecondary.copy(alpha = .18f)
            Column(Modifier.fillMaxWidth().padding(IFSpacing.panel),
                verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
                Text(stringResource(R.string.student_coefficient),
                    style = IFTheme.typography.labelSm, color = IFTheme.colors.performanceSecondary)
                FlowRow(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(IFSpacing.sm),
                    itemVerticalAlignment = Alignment.CenterVertically,
                ) {
                    IFSkeletonLine(1f, Modifier.width(88.dp),
                        style = if (IFTheme.pageInset == IFSpacing.pageInsetNarrow)
                            IFTheme.typography.coefficientNarrow else IFTheme.typography.coefficient,
                        color = placeholder)
                    Column(Modifier.widthIn(max = 180.dp), horizontalAlignment = Alignment.End) {
                        Text(stringResource(R.string.class_coefficient),
                            style = IFTheme.typography.labelSm,
                            color = IFTheme.colors.performanceSecondary)
                        IFSkeletonLine(1f, Modifier.width(56.dp),
                            style = IFTheme.typography.coefficientComparison, color = placeholder)
                    }
                }
            }
        }
        Spacer(Modifier.height(IFSpacing.lg))
        FlowRow(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(IFSpacing.xs),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            IFSectionTitle(stringResource(R.string.today_classes))
            IFTextButton({}, enabled = false) {
                Text(stringResource(R.string.timetable))
                androidx.compose.material3.Icon(IFIcon.Arrow.vector, null, Modifier.size(IFSize.icon))
            }
        }
        repeat(3) { ClassRowSkeleton(showTeacher = false) }
    }
}

@Composable
internal fun TimetableLoading(period: String?) {
    IFLoadingRegion(stringResource(R.string.loading_timetable)) {
        if (period == null) IFSkeletonLine(.4f)
        else Text(period, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
        Spacer(Modifier.height(IFSpacing.md))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
            repeat(7) {
                Box(Modifier.weight(1f).heightIn(min = IFSize.touchTarget), contentAlignment = Alignment.Center) {
                    IFSkeletonLine(.65f, style = IFTheme.typography.labelMd)
                }
            }
        }
        repeat(2) { ClassRowSkeleton() }
    }
}

@Composable
private fun ClassRowSkeleton(showTeacher: Boolean = true) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = IFSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(IFSpacing.sm),
    ) {
        Column(Modifier.width(52.dp), verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
            IFSkeletonLine(.9f, style = IFTheme.typography.labelMd)
            IFSkeletonLine(.9f)
        }
        IFSkeletonBlock(Modifier.width(2.dp).height(48.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
            IFSkeletonLine(.85f, style = IFTheme.typography.detailTitle)
            IFSkeletonLine(.5f)
            if (showTeacher) IFSkeletonLine(.7f)
        }
    }
    HorizontalDivider(color = IFTheme.colors.outline)
}
