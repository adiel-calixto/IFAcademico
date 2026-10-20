package com.adielcalixto.ifacademico.presentation.diary_list.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.*

@Composable
internal fun SubjectsLoading(period: String?) {
    IFLoadingRegion(stringResource(R.string.loading_subjects)) {
        if (period == null) IFSkeletonLine(.25f, style = IFTheme.typography.titleSm)
        else IFSectionTitle(period)
        Spacer(Modifier.height(IFSpacing.xs))
        repeat(3) {
            Column(Modifier.fillMaxWidth().padding(vertical = IFSpacing.md),
                verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
                IFSkeletonLine(.85f, style = IFTheme.typography.detailTitle)
                IFSkeletonLine(.65f)
                IFSkeletonLine(.35f, style = IFTheme.typography.labelSm)
            }
            HorizontalDivider(color = IFTheme.colors.outline)
        }
    }
}
