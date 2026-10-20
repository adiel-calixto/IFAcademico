package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme

@Composable
fun IFLoadingRegion(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier.fillMaxWidth().semantics {
            contentDescription = label
            liveRegion = LiveRegionMode.Polite
            progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
        },
        content = content,
    )
}

@Composable
fun IFSkeletonBlock(
    modifier: Modifier,
    shape: Shape = IFShapes.xs,
    color: Color = IFTheme.colors.surfaceSubtle,
) {
    Box(modifier.clip(shape).background(color))
}

/** Reserve the real line box, including font scaling, without rendering fake text. */
@Composable
fun IFSkeletonLine(
    fraction: Float,
    modifier: Modifier = Modifier,
    style: TextStyle = IFTheme.typography.bodySm,
    color: Color = IFTheme.colors.surfaceSubtle,
) {
    val density = LocalDensity.current
    val lineHeight = with(density) { style.lineHeight.toDp() }
    val barHeight = with(density) { style.fontSize.toDp() * .65f }
    Box(
        modifier.fillMaxWidth(fraction).height(lineHeight),
        contentAlignment = Alignment.CenterStart
    ) {
        IFSkeletonBlock(Modifier.fillMaxWidth().height(barHeight), color = color)
    }
}

@Composable
fun IFEmptyState(message: String, modifier: Modifier = Modifier) {
    Text(
        message, modifier.padding(vertical = IFSpacing.md),
        style = IFTheme.typography.bodyMd, color = IFTheme.colors.secondary
    )
}

/** Fills only the failed region; page/section insets belong to the caller. */
@Composable
fun IFErrorState(
    message: String, onRetry: () -> Unit, modifier: Modifier = Modifier,
    title: String = stringResource(R.string.content_load_error),
) {
    Surface(
        modifier.fillMaxWidth(),
        color = IFTheme.colors.surfaceSubtle,
        shape = IFTheme.shapes.lg
    ) {
        Column(
            Modifier.padding(IFSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(IFSpacing.sm),
        ) {
            Icon(IFIcon.Warning.vector, null, Modifier.size(28.dp), tint = IFTheme.colors.error)
            Column(
                Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(IFSpacing.xs),
            ) {
                Text(
                    title, style = IFTheme.typography.titleSm,
                    color = IFTheme.colors.onSurface, textAlign = TextAlign.Center
                )
                Text(
                    message, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary,
                    textAlign = TextAlign.Center
                )
            }
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.heightIn(min = IFSize.touchTarget),
                shape = IFTheme.shapes.md,
                border = BorderStroke(1.dp, IFTheme.colors.primary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = IFTheme.colors.primary),
            ) { Text(stringResource(R.string.retry)) }
        }
    }
}

@Composable
fun IFToast(message: String, modifier: Modifier = Modifier) {
    Surface(
        modifier.semantics { liveRegion = LiveRegionMode.Polite },
        color = IFTheme.colors.toast, contentColor = IFTheme.colors.onToast,
        shape = IFShapes.md, shadowElevation = 4.dp
    ) {
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
        IFButton(
            onConfirm,
            Modifier.fillMaxWidth(),
            destructive = destructive
        ) { Text(confirmLabel) }
        IFTextButton(onCancel, Modifier.fillMaxWidth()) { Text(stringResource(R.string.cancel)) }
    }
}
