@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.Theme
import com.adielcalixto.ifacademico.presentation.student_info.components.IFThemeSelector
import com.adielcalixto.ifacademico.presentation.student_info.components.IFUpdateSwitch
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ComponentInteractionTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun disabledButtonDoesNotInvokeActionAndSwitchExposesItsState() {
        var invocations = 0
        compose.setContent {
            IFTheme {
                var checked by remember { mutableStateOf(false) }
                Column {
                    IFButton({ invocations++ }, enabled = false) { Text("Disabled action") }
                    IFUpdateSwitch(checked, { checked = it }, label = "Updates")
                }
            }
        }
        compose.onNodeWithText("Disabled action").assertIsNotEnabled().performTouchInput { click() }
        compose.runOnIdle { assertEquals(0, invocations) }
        compose.onNodeWithContentDescription("Updates").assertIsOff().performClick().assertIsOn()
    }

    @Test
    fun fractionalNavigationIndicatorFitsWrappedLabelsInBothDirectionsAndThemes() {
        var rtl by mutableStateOf(false)
        var dark by mutableStateOf(false)
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, 2f),
                LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
            ) {
                IFTheme(darkTheme = dark) {
                    IFBottomNavigation(
                        items = listOf(
                            IFNavigationItem("Today", IFIcon.Home),
                            IFNavigationItem("Academic diaries", IFIcon.Grades),
                            IFNavigationItem("Calendar", IFIcon.Calendar),
                        ),
                        selectedIndex = 1,
                        onSelect = {},
                        modifier = Modifier.width(320.dp),
                        indicatorPosition = .5f,
                    )
                }
            }
        }
        for (isDark in listOf(false, true)) {
            for (isRtl in listOf(false, true)) {
                compose.runOnIdle { dark = isDark; rtl = isRtl }
                val today = compose.onNodeWithText("Today").fetchSemanticsNode().boundsInRoot
                val diaries = compose.onNodeWithText("Academic diaries").fetchSemanticsNode().boundsInRoot
                val indicator = compose.onNodeWithTag(
                    "bottom-navigation-indicator", useUnmergedTree = true,
                ).fetchSemanticsNode().boundsInRoot
                assertEquals((today.center.x + diaries.center.x) / 2f, indicator.center.x, 2f)
                assertEquals(today.width, indicator.width, 2f)
                assertEquals(maxOf(today.height, diaries.height), indicator.height, 1f)
                compose.onNodeWithText("Academic diaries").assertIsSelected()
            }
        }
    }

    @Test
    fun navigationAndThemeSelectorExposeSelection() {
        compose.setContent {
            IFTheme {
                var selected by remember { mutableIntStateOf(0) }
                var theme by remember { mutableStateOf(Theme.SYSTEM) }
                Column {
                    IFBottomNavigation(listOf(
                        IFNavigationItem("Today", IFIcon.Home), IFNavigationItem("Grades", IFIcon.Grades),
                        IFNavigationItem("Calendar", IFIcon.Calendar)), selected, { selected = it })
                    IFThemeSelector(theme, { theme = it })
                }
            }
        }
        compose.onNodeWithText("Today").assertIsSelected()
        compose.onNodeWithText("Grades").performClick().assertIsSelected()
        compose.onNodeWithText("Today").assertIsNotSelected()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val light = context.getString(R.string.light_theme)
        val system = context.getString(R.string.system_theme)
        val themeLabel = context.getString(R.string.theme)
        compose.onNodeWithText("$themeLabel: $system").performClick()
        compose.onNodeWithText(system).assertIsSelected()
        compose.onNodeWithText(light).performClick()
        compose.onNodeWithText("$themeLabel: $light").assertExists()
    }

    @Test
    fun sheetClosesAndReturnsKeyboardFocusToItsOpener() {
        compose.setContent {
            IFTheme {
                var open by remember { mutableStateOf(false) }
                val opener = remember { FocusRequester() }
                val inputMode = LocalInputModeManager.current
                LaunchedEffect(Unit) { inputMode.requestInputMode(InputMode.Keyboard) }
                IFButton({ open = true }, Modifier.focusRequester(opener)) { Text("Open sheet") }
                if (open) IFSlidingSheet("Details", { open = false }, returnFocus = opener) {
                    Text("Read-only detail")
                }
            }
        }
        compose.onNodeWithText("Open sheet").performClick()
        compose.onNodeWithText("Read-only detail").assertIsDisplayed()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        compose.onNodeWithContentDescription(context.getString(R.string.close)).assertIsFocused()
        compose.onNode(isDialog()).performKeyInput { repeat(3) { pressKey(Key.Tab) } }
        compose.onNodeWithText("Open sheet").assertIsNotFocused()
        compose.onNodeWithContentDescription(context.getString(R.string.close)).performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Read-only detail").assertDoesNotExist()
        compose.waitUntil(5_000) {
            compose.onNodeWithText("Open sheet").fetchSemanticsNode()
                .config[androidx.compose.ui.semantics.SemanticsProperties.Focused]
        }
        compose.onNodeWithText("Open sheet").assertIsFocused()
    }

    @Test
    fun cancelConfirmationDoesNotPerformDestructiveAction() {
        var confirmed = false
        var cancelled = false
        compose.setContent {
            IFTheme {
                IFConfirmationContent("End session?", "Log out", { confirmed = true }, { cancelled = true })
            }
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        compose.onNodeWithText(context.getString(R.string.cancel)).performClick()
        compose.runOnIdle {
            assertFalse(confirmed)
            assertTrue(cancelled)
        }
    }
}
