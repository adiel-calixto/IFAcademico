package com.adielcalixto.ifacademico.presentation.calendar.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.*

@Composable
internal fun EventsLoading() {
    IFLoadingRegion(stringResource(R.string.loading_special_dates)) {
        repeat(3) {
            Row(Modifier.fillMaxWidth().padding(vertical = IFSpacing.micro),
                horizontalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
                IFSkeletonLine(.8f, Modifier.width(64.dp))
                Column(Modifier.weight(1f)) {
                    IFSkeletonLine(.95f)
                    IFSkeletonLine(.6f)
                }
            }
            HorizontalDivider(color = IFTheme.colors.outline)
        }
    }
}
