package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme

@Composable
fun IFSessionLoading(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(IFTheme.pageInset), contentAlignment = Alignment.Center) {
        Column(
            Modifier.semantics(mergeDescendants = true) {
                liveRegion = LiveRegionMode.Polite
                progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
            },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(IFSpacing.md),
        ) {
            CircularProgressIndicator(
                Modifier.size(28.dp).clearAndSetSemantics {},
                color = IFTheme.colors.primary,
                strokeWidth = 3.dp,
            )
            Text(
                stringResource(R.string.checking_session),
                style = IFTheme.typography.bodySm,
                color = IFTheme.colors.secondary,
            )
        }
    }
}
