package com.adielcalixto.ifacademico.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo

private val LocalColors = staticCompositionLocalOf { LightColors }
private val LocalDarkTheme = staticCompositionLocalOf { false }
private val Type = IFTypography()

object IFTheme {
    val colors: IFColors @Composable @ReadOnlyComposable get() = LocalColors.current
    val typography: IFTypography get() = Type
    val spacing get() = IFSpacing
    val shapes get() = IFShapes
    val isDark: Boolean @Composable @ReadOnlyComposable get() = LocalDarkTheme.current
    val pageInset @Composable @ReadOnlyComposable get() =
        if (LocalWindowInfo.current.containerSize.width / LocalDensity.current.density <= 375f)
            IFSpacing.pageInsetNarrow else IFSpacing.pageInset
}

/** Pure provider: no preferences, network access, view models, or Activity required. */
@Composable
fun IFTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val c = if (darkTheme) DarkColors else LightColors
    val base = if (darkTheme) darkColorScheme() else lightColorScheme()
    val scheme = base.copy(
        primary = c.primary, onPrimary = c.onPrimary,
        primaryContainer = c.primaryContainer, onPrimaryContainer = c.primary,
        secondary = c.secondary, onSecondary = c.background,
        secondaryContainer = c.surfaceSubtle, onSecondaryContainer = c.onSurface,
        tertiary = c.onNotice, onTertiary = c.notice,
        tertiaryContainer = c.notice, onTertiaryContainer = c.onNotice,
        error = c.error, onError = c.background,
        errorContainer = c.surfaceSubtle, onErrorContainer = c.error,
        background = c.background, onBackground = c.onSurface,
        surface = c.surface, onSurface = c.onSurface,
        surfaceVariant = c.surfaceSubtle, onSurfaceVariant = c.secondary,
        surfaceTint = c.primary, outline = c.inputOutline, outlineVariant = c.outline,
        inverseSurface = c.toast, inverseOnSurface = c.onToast,
        inversePrimary = if (darkTheme) LightColors.primary else DarkColors.primary,
        scrim = c.scrim,
        surfaceDim = c.background, surfaceBright = c.surface,
        surfaceContainerLowest = c.background, surfaceContainerLow = c.background,
        surfaceContainer = c.surface, surfaceContainerHigh = c.surfaceSubtle,
        surfaceContainerHighest = c.surfaceSubtle,
    )
    CompositionLocalProvider(LocalColors provides c, LocalDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = scheme, typography = Type.material,
            shapes = Shapes(IFShapes.xs, IFShapes.sm, IFShapes.md, IFShapes.lg, IFShapes.sheet),
            content = content,
        )
    }
}
