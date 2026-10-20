package com.adielcalixto.ifacademico.presentation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.components.IFButton
import com.adielcalixto.ifacademico.presentation.components.IFSlidingSheet
import com.adielcalixto.ifacademico.presentation.components.IFTextButton
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SignedInShellInteractionTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private fun label(id: Int) = context.getString(id)
    private val tabRole = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)
    private fun tab(id: Int) = compose.onNode(tabRole and hasText(label(id)))
    private fun page(tab: MainTab) = compose.onNodeWithTag("page-${tab.name}")

    @Composable
    private fun Host(dark: Boolean = false) {
        IFTheme(darkTheme = dark) {
            // Models are represented by state outside the lazy page compositions.
            val period = remember { mutableStateOf("2026/1") }
            val sheetOpen = remember { mutableStateOf(false) }
            SignedInShell(
                periodControl = { focus ->
                    IFTextButton({ sheetOpen.value = true }, Modifier.focusRequester(focus)) {
                        Text(period.value)
                    }
                },
                pageContent = { tab, focus ->
                    val scroll = rememberScrollState()
                    Column(
                        Modifier.fillMaxSize().testTag("page-${tab.name}")
                            .verticalScroll(scroll),
                    ) {
                        repeat(30) { index ->
                            Text("${tab.name} record $index", Modifier.height(64.dp).padding(8.dp))
                        }
                    }
                    if (tab == MainTab.Diaries && sheetOpen.value) {
                        IFSlidingSheet("Academic period", { sheetOpen.value = false }, returnFocus = focus) {
                            IFButton({ period.value = "2026/2"; sheetOpen.value = false }) {
                                Text("Choose 2026/2")
                            }
                        }
                    }
                },
                profileContent = { Text("Profile content", Modifier.fillMaxSize()) },
            )
        }
    }

    @Test
    fun swipesAndTabTapsSelectPagesWithoutWrapping() {
        compose.setContent { Host() }
        tab(R.string.today_label).assertIsSelected()
        page(MainTab.Today).performTouchInput { swipeRight() }
        tab(R.string.today_label).assertIsSelected()
        page(MainTab.Today).performTouchInput { swipeLeft() }
        tab(R.string.grades_label).assertIsSelected()
        page(MainTab.Diaries).performTouchInput { swipeLeft() }
        tab(R.string.calendar).assertIsSelected()
        page(MainTab.Calendar).performTouchInput { swipeLeft() }
        tab(R.string.calendar).assertIsSelected()
        tab(R.string.today_label).performClick().assertIsSelected()
        tab(R.string.calendar).performClick().assertIsSelected()
        page(MainTab.Calendar).performTouchInput { swipeRight() }
        tab(R.string.grades_label).assertIsSelected()
    }

    @Test
    fun canceledDragKeepsSelectionAndPagesFollowTheFinger() {
        compose.setContent { Host() }
        compose.mainClock.autoAdvance = false
        page(MainTab.Today).performTouchInput {
            down(center)
            moveBy(androidx.compose.ui.geometry.Offset(-width * .2f, 0f), 300)
        }
        compose.mainClock.advanceTimeByFrame()
        val position = page(MainTab.Today).fetchSemanticsNode().positionInRoot
        assertTrue(position.x < 0f)
        tab(R.string.today_label).assertIsSelected()
        page(MainTab.Today).performTouchInput { cancel() }
        compose.mainClock.autoAdvance = true
        tab(R.string.today_label).assertIsSelected()
        page(MainTab.Today).assertIsDisplayed()
    }

    @Test
    fun nonAdjacentTapSlidesThroughMiddlePageAndLatestTapWins() {
        compose.setContent { Host() }
        compose.mainClock.autoAdvance = false
        tab(R.string.calendar).performClick()
        compose.mainClock.advanceTimeBy(112)
        // The target tab is not selected until the pager finishes its movement.
        tab(R.string.today_label).assertIsSelected()
        page(MainTab.Diaries).assertExists()
        tab(R.string.grades_label).performClick()
        compose.mainClock.autoAdvance = true
        tab(R.string.grades_label).assertIsSelected()
    }

    @Test
    fun verticalScrollAndProfileReturnPreserveEachTab() {
        compose.setContent { Host(dark = true) }
        page(MainTab.Today).performScrollToNode(hasText("Today record 15"))
        val before = compose.onNodeWithText("Today record 15").fetchSemanticsNode().boundsInRoot.top
        tab(R.string.calendar).performClick()
        page(MainTab.Calendar).performScrollToNode(hasText("Calendar record 15"))
        compose.onNodeWithContentDescription(label(R.string.open_profile)).performClick()
        compose.onNodeWithText("Profile content").assertIsDisplayed()
        tab(R.string.calendar).assertIsNotSelected()
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        tab(R.string.calendar).assertIsSelected()
        compose.onNodeWithText("Calendar record 15").assertIsDisplayed()
        tab(R.string.today_label).performClick()
        compose.onNodeWithText("Today record 15").assertIsDisplayed()
        assertEquals(before, compose.onNodeWithText("Today record 15").fetchSemanticsNode().boundsInRoot.top, 1f)
    }

    @Test
    fun backReturnsToTodayAndTabTapLeavesProfile() {
        compose.setContent { Host() }
        tab(R.string.calendar).performClick()
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        tab(R.string.today_label).assertIsSelected()
        compose.onNodeWithContentDescription(label(R.string.open_profile)).performClick()
        compose.onNodeWithText("Profile content").assertIsDisplayed()
        tab(R.string.grades_label).performClick().assertIsSelected()
        compose.onNodeWithText("Profile content").assertDoesNotExist()
        compose.onNodeWithText("2026/1").assertIsDisplayed()
    }

    @Test
    fun periodSheetBlocksPagerAndSelectionSurvivesTabChanges() {
        compose.setContent { Host() }
        tab(R.string.grades_label).performClick()
        compose.onNodeWithText("2026/1").performClick()
        compose.onNodeWithText("Academic period").assertIsDisplayed()
        compose.onNodeWithText("Choose 2026/2").performTouchInput { swipeLeft() }
        compose.onNodeWithText("Academic period").assertIsDisplayed()
        compose.onNodeWithText("Choose 2026/2").performClick()
        compose.onNodeWithText("2026/2").assertIsDisplayed()
        tab(R.string.calendar).performClick()
        compose.onNodeWithText("2026/2").assertDoesNotExist()
        tab(R.string.grades_label).performClick()
        compose.onNodeWithText("2026/2").assertIsDisplayed()
    }

    @Test
    fun enlargedTextKeepsTabsAndProfileAccessible() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                Host(dark = true)
            }
        }
        tab(R.string.calendar).assertIsDisplayed().performClick().assertIsSelected()
        page(MainTab.Calendar).performScrollToNode(hasText("Calendar record 15"))
        compose.onNodeWithContentDescription(label(R.string.open_profile))
            .assertIsDisplayed().performClick()
        tab(R.string.grades_label).assertIsDisplayed().performClick().assertIsSelected()
        compose.onNodeWithText("2026/1").assertIsDisplayed()
    }

    @Test
    fun aNewSessionStartsOnToday() {
        var session by mutableIntStateOf(0)
        compose.setContent { key(session) { Host() } }
        tab(R.string.calendar).performClick().assertIsSelected()
        compose.runOnIdle { session++ }
        tab(R.string.today_label).assertIsSelected()
    }

    @Test
    fun savedTabRestores() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent { Host() }
        tab(R.string.calendar).performClick()
        restoration.emulateSavedInstanceStateRestore()
        tab(R.string.calendar).assertIsSelected()
    }
}
