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
import androidx.hilt.navigation.compose.hiltViewModel

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

enum class Theme(val id: String) {
    LIGHT("light"),
    DARK("dark"),
    PURPLE("purple");

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
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surfaceContainer.toArgb()
            window.navigationBarColor = colorScheme.surfaceContainer.toArgb()
            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightStatusBars = theme == Theme.LIGHT
            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightNavigationBars = theme == Theme.LIGHT
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}