package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme

@Composable
fun IFLoadingPlaceholder(modifier: Modifier = Modifier, rows: Int = 3) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
        Text(stringResource(R.string.loading_records), Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
        repeat(rows.coerceAtLeast(0)) {
            Box(Modifier.fillMaxWidth(if (it % 2 == 0) 1f else .75f)
                .height(48.dp).clip(IFShapes.md).background(IFTheme.colors.surfaceSubtle))
        }
    }
}

@Composable
fun IFEmptyState(message: String, modifier: Modifier = Modifier) {
    Text(message, modifier.padding(vertical = IFSpacing.md),
        style = IFTheme.typography.bodyMd, color = IFTheme.colors.secondary)
}

@Composable
fun IFErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
        Text(message, Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
            style = IFTheme.typography.bodyMd, color = IFTheme.colors.onSurface)
        IFButton(onRetry) { Text(stringResource(R.string.retry)) }
    }
}

@Composable
fun IFToast(message: String, modifier: Modifier = Modifier) {
    Surface(modifier.semantics { liveRegion = LiveRegionMode.Polite },
        color = IFTheme.colors.toast, contentColor = IFTheme.colors.onToast,
        shape = IFShapes.md, shadowElevation = 4.dp) {
        Text(message, Modifier.padding(IFSpacing.md), style = IFTheme.typography.bodySm)
    }
}

@Composable
fun IFConfirmationContent(
    message: String, confirmLabel: String, onConfirm: () -> Unit, onCancel: () -> Unit,
    modifier: Modifier = Modifier, destructive: Boolean = true,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
        Text(message, style = IFTheme.typography.bodyMd)
        IFButton(onConfirm, Modifier.fillMaxWidth(), destructive = destructive) { Text(confirmLabel) }
        IFTextButton(onCancel, Modifier.fillMaxWidth()) { Text(stringResource(R.string.cancel)) }
    }
}
