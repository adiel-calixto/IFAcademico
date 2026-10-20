package com.adielcalixto.ifacademico.presentation.student_info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
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
internal fun StudentInfoLoading(modifier: Modifier = Modifier) {
    IFLoadingRegion(
        stringResource(R.string.loading_profile),
        modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = IFTheme.pageInset, vertical = IFSpacing.lg),
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            IFSkeletonBlock(Modifier.size(IFSize.avatar), shape = IFTheme.shapes.full)
            Spacer(Modifier.height(IFSpacing.md))
            IFSkeletonLine(.7f, style = IFTheme.typography.titleLg)
            Spacer(Modifier.height(IFSpacing.micro))
            IFSkeletonLine(.85f)
        }
        Spacer(Modifier.height(IFSpacing.xl))
        Surface(color = IFTheme.colors.surface, shape = IFTheme.shapes.lg) {
            Column(Modifier.fillMaxWidth().padding(IFSpacing.panel),
                verticalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
                IFSectionTitle(stringResource(R.string.preferences))
                repeat(2) {
                    Row(Modifier.fillMaxWidth().heightIn(min = IFSize.touchTarget),
                        horizontalArrangement = Arrangement.spacedBy(IFSpacing.md),
                        verticalAlignment = Alignment.CenterVertically) {
                        IFSkeletonLine(.75f, Modifier.weight(1f), style = IFTheme.typography.labelMd)
                        IFSkeletonBlock(Modifier.width(72.dp).height(24.dp),
                            color = IFTheme.colors.outline)
                    }
                    HorizontalDivider(color = IFTheme.colors.outline)
                }
                Box(Modifier.fillMaxWidth().heightIn(min = IFSize.touchTarget),
                    contentAlignment = Alignment.CenterEnd) {
                    IFSkeletonLine(1f, Modifier.width(72.dp), style = IFTheme.typography.labelMd)
                }
            }
        }
    }
}
