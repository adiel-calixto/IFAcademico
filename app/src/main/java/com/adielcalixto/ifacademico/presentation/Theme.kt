package com.adielcalixto.ifacademico.presentation

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

val purpleScheme = darkColorScheme(
    primary = primaryPurple,
    onPrimary = onPrimaryPurple,
    primaryContainer = primaryContainerPurple,
    onPrimaryContainer = onPrimaryContainerPurple,
    secondary = secondaryPurple,
    onSecondary = onSecondaryPurple,
    secondaryContainer = secondaryContainerPurple,
    onSecondaryContainer = onSecondaryContainerPurple,
    tertiary = tertiaryPurple,
    onTertiary = onTertiaryPurple,
    tertiaryContainer = tertiaryContainerPurple,
    onTertiaryContainer = onTertiaryContainerPurple,
    error = errorPurple,
    onError = onErrorPurple,
    errorContainer = errorContainerPurple,
    onErrorContainer = onErrorContainerPurple,
    background = backgroundPurple,
    onBackground = onBackgroundPurple,
    surface = surfacePurple,
    onSurface = onSurfacePurple,
    surfaceVariant = surfaceVariantPurple,
    onSurfaceVariant = onSurfaceVariantPurple,
    outline = outlinePurple,
    outlineVariant = outlineVariantPurple,
    scrim = scrimPurple,
    inverseSurface = inverseSurfacePurple,
    inverseOnSurface = inverseOnSurfacePurple,
    inversePrimary = inversePrimaryPurple,
    surfaceDim = surfaceDimPurple,
    surfaceBright = surfaceBrightPurple,
    surfaceContainerLowest = surfaceContainerLowestPurple,
    surfaceContainerLow = surfaceContainerLowPurple,
    surfaceContainer = surfaceContainerPurple,
    surfaceContainerHigh = surfaceContainerHighPurple,
    surfaceContainerHighest = surfaceContainerHighestPurple,
)

private val yellowScheme = lightColorScheme(
    primary = primaryYellow,
    onPrimary = onPrimaryYellow,
    primaryContainer = primaryContainerYellow,
    onPrimaryContainer = onPrimaryContainerYellow,
    secondary = secondaryYellow,
    onSecondary = onSecondaryYellow,
    secondaryContainer = secondaryContainerYellow,
    onSecondaryContainer = onSecondaryContainerYellow,
    tertiary = tertiaryYellow,
    onTertiary = onTertiaryYellow,
    tertiaryContainer = tertiaryContainerYellow,
    onTertiaryContainer = onTertiaryContainerYellow,
    error = errorYellow,
    onError = onErrorYellow,
    errorContainer = errorContainerYellow,
    onErrorContainer = onErrorContainerYellow,
    background = backgroundYellow,
    onBackground = onBackgroundYellow,
    surface = surfaceYellow,
    onSurface = onSurfaceYellow,
    surfaceVariant = surfaceVariantYellow,
    onSurfaceVariant = onSurfaceVariantYellow,
    outline = outlineYellow,
    outlineVariant = outlineVariantYellow,
    scrim = scrimYellow,
    inverseSurface = inverseSurfaceYellow,
    inverseOnSurface = inverseOnSurfaceYellow,
    inversePrimary = inversePrimaryYellow,
    surfaceDim = surfaceDimYellow,
    surfaceBright = surfaceBrightYellow,
    surfaceContainerLowest = surfaceContainerLowestYellow,
    surfaceContainerLow = surfaceContainerLowYellow,
    surfaceContainer = surfaceContainerYellow,
    surfaceContainerHigh = surfaceContainerHighYellow,
    surfaceContainerHighest = surfaceContainerHighestYellow,
)

private val blueScheme = lightColorScheme(
    primary = primaryBlue,
    onPrimary = onPrimaryBlue,
    primaryContainer = primaryContainerBlue,
    onPrimaryContainer = onPrimaryContainerBlue,
    secondary = secondaryBlue,
    onSecondary = onSecondaryBlue,
    secondaryContainer = secondaryContainerBlue,
    onSecondaryContainer = onSecondaryContainerBlue,
    tertiary = tertiaryBlue,
    onTertiary = onTertiaryBlue,
    tertiaryContainer = tertiaryContainerBlue,
    onTertiaryContainer = onTertiaryContainerBlue,
    error = errorBlue,
    onError = onErrorBlue,
    errorContainer = errorContainerBlue,
    onErrorContainer = onErrorContainerBlue,
    background = backgroundBlue,
    onBackground = onBackgroundBlue,
    surface = surfaceBlue,
    onSurface = onSurfaceBlue,
    surfaceVariant = surfaceVariantBlue,
    onSurfaceVariant = onSurfaceVariantBlue,
    outline = outlineBlue,
    outlineVariant = outlineVariantBlue,
    scrim = scrimBlue,
    inverseSurface = inverseSurfaceBlue,
    inverseOnSurface = inverseOnSurfaceBlue,
    inversePrimary = inversePrimaryBlue,
    surfaceDim = surfaceDimBlue,
    surfaceBright = surfaceBrightBlue,
    surfaceContainerLowest = surfaceContainerLowestBlue,
    surfaceContainerLow = surfaceContainerLowBlue,
    surfaceContainer = surfaceContainerBlue,
    surfaceContainerHigh = surfaceContainerHighBlue,
    surfaceContainerHighest = surfaceContainerHighestBlue,
)

enum class Theme(val id: String) {
    LIGHT("light"),
    DARK("dark"),
    PURPLE("purple"),
    YELLOW("yellow"),
    BLUE("blue");

    companion object {
        infix fun from(id: String): Theme? = entries.firstOrNull { it.id == id }
    }
}

@Composable
fun AppTheme(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable() () -> Unit
) {
    val theme by themeViewModel.theme.collectAsState()

    val colorScheme = when (theme) {
        Theme.LIGHT -> lightScheme
        Theme.DARK -> darkScheme
        Theme.PURPLE -> purpleScheme
        Theme.YELLOW -> yellowScheme
        Theme.BLUE -> blueScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surfaceContainer.toArgb()
            window.navigationBarColor = colorScheme.surfaceContainer.toArgb()
            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightStatusBars = theme == Theme.LIGHT || theme == Theme.YELLOW || theme == Theme.BLUE
            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightNavigationBars = theme == Theme.LIGHT || theme == Theme.YELLOW || theme == Theme.BLUE
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}