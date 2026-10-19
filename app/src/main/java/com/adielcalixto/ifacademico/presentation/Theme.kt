package com.adielcalixto.ifacademico.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adielcalixto.ifacademico.presentation.designsystem.IFTheme

enum class Theme(val id: String) {
    SYSTEM("system"), LIGHT("light"), DARK("dark");

    companion object {
        /** Retired palettes preserve the brightness of the saved appearance. */
        infix fun from(id: String): Theme = when (id) {
            "light", "yellow", "blue" -> LIGHT
            "dark", "purple" -> DARK
            else -> SYSTEM
        }
    }
}

fun Theme.isDark(systemDark: Boolean): Boolean = when (this) {
    Theme.SYSTEM -> systemDark
    Theme.LIGHT -> false
    Theme.DARK -> true
}

private tailrec fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.activity()
    else -> null
}

/** Preference/window integration. Use IFTheme directly in previews and tests. */
@Composable
fun AppTheme(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val theme by themeViewModel.theme.collectAsStateWithLifecycle()
    val dark = theme.isDark(isSystemInDarkTheme())
    val view = LocalView.current
    val activity = LocalContext.current.activity()
    if (!view.isInEditMode && activity != null) {
        SideEffect {
            WindowCompat.getInsetsController(activity.window, view).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
        }
    }
    IFTheme(darkTheme = dark, content = content)
}
