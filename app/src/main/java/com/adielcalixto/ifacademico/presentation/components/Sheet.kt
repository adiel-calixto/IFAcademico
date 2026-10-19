package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFMotion
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme

/**
 * Dialog provides a separate modal focus scope and Back handling. The fixed header
 * keeps close/back available while only the body scrolls. Pass the opener's focus
 * requester when keyboard focus should return to a particular control.
 * Compose animation honors Android's animator duration scale (including zero).
 */
@Composable
fun IFSlidingSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    returnFocus: FocusRequester? = null,
    scrollState: ScrollState = rememberScrollState(),
    titleStyle: TextStyle = IFTheme.typography.headlineSm,
    content: @Composable ColumnScope.() -> Unit,
) {
    val hostView = LocalView.current
    val hostDensity = LocalDensity.current
    val transition = remember { MutableTransitionState(false).apply { targetState = true } }
    val closeFocus = remember { FocusRequester() }
    val currentDismiss by rememberUpdatedState(onDismiss)
    val currentReturnFocus by rememberUpdatedState(returnFocus)
    val requestDismiss = { transition.targetState = false }
    LaunchedEffect(transition.currentState, transition.isIdle) {
        if (transition.isIdle && !transition.currentState && !transition.targetState) currentDismiss()
    }
    DisposableEffect(Unit) {
        onDispose {
            val requester = currentReturnFocus
            if (requester != null && !transition.targetState && hostView.isAttachedToWindow) {
                // Window focus returns asynchronously, after Dialog disposal.
                val restore = {
                    hostView.post {
                        if (hostView.isAttachedToWindow) {
                            try {
                                requester.requestFocus()
                            } catch (_: IllegalStateException) {
                                // The dismiss callback may have removed the opener.
                            }
                        }
                    }
                }
                if (hostView.hasWindowFocus()) restore() else {
                    hostView.viewTreeObserver.addOnWindowFocusChangeListener(
                        object : android.view.ViewTreeObserver.OnWindowFocusChangeListener {
                            override fun onWindowFocusChanged(hasFocus: Boolean) {
                                if (hasFocus) {
                                    hostView.viewTreeObserver.removeOnWindowFocusChangeListener(this)
                                    restore()
                                }
                            }
                        },
                    )
                }
            }
        }
    }
    Dialog(
        onDismissRequest = { if (onBack != null) onBack() else requestDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        // Dialog owns another AndroidComposeView; preserve caller text scaling.
        CompositionLocalProvider(LocalDensity provides hostDensity) {
            val dialogView = LocalView.current
            val dark = IFTheme.isDark
            SideEffect {
                (dialogView.parent as? DialogWindowProvider)?.window?.let { window ->
                    window.setDimAmount(0f) // The design-system scrim supplies the complete tint.
                    WindowCompat.getInsetsController(window, dialogView).apply {
                        isAppearanceLightStatusBars = false // Status bar sits over the dark scrim.
                        isAppearanceLightNavigationBars = !dark
                    }
                }
            }
            BoxWithConstraints(Modifier.fillMaxSize()) {
                // This handles pointer dismissal; the named close button handles keyboard/accessibility.
                Box(Modifier.fillMaxSize().background(IFTheme.colors.scrim)
                    .pointerInput(transition) {
                        detectTapGestures { transition.targetState = false }
                    })
                AnimatedVisibility(
                    visibleState = transition, modifier = Modifier.align(Alignment.BottomCenter),
                    enter = slideInVertically(tween(IFMotion.sheetMillis)) { it } + fadeIn(tween(IFMotion.sheetMillis)),
                    exit = slideOutVertically(tween(IFMotion.sheetMillis)) { it } + fadeOut(tween(IFMotion.sheetMillis)),
                ) {
                    Surface(
                        modifier.fillMaxWidth().heightIn(max = maxHeight * .9f)
                            .imePadding().semantics { paneTitle = title },
                        shape = IFShapes.sheet, color = IFTheme.colors.background,
                    ) {
                        Column(Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))) {
                            Row(Modifier.fillMaxWidth().padding(horizontal = IFSpacing.lg, vertical = IFSpacing.sm),
                                verticalAlignment = Alignment.CenterVertically) {
                                onBack?.let {
                                    IFIconButton(it) { Icon(IFIcon.Back.vector, stringResource(R.string.back), Modifier.size(IFSize.icon)) }
                                }
                                Text(title, Modifier.weight(1f).semantics { heading() }, style = titleStyle)
                                IFIconButton(requestDismiss, Modifier.focusRequester(closeFocus)) {
                                    Icon(IFIcon.Close.vector, stringResource(R.string.close), Modifier.size(IFSize.icon))
                                }
                            }
                            Column(Modifier.weight(1f, fill = false).verticalScroll(scrollState)
                                .padding(start = IFSpacing.lg, end = IFSpacing.lg, bottom = IFSpacing.lg), content = content)
                        }
                    }
                    LaunchedEffect(title) { closeFocus.requestFocus() }
                }
            }
        }
    }
}
