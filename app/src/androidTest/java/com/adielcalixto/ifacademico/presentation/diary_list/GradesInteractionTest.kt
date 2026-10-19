package com.adielcalixto.ifacademico.presentation.diary_list

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.IFHeader
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class GradesInteractionTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private fun label(id: Int) = context.getString(id)

    @Composable
    private fun Host(state: DiaryListState, onAction: (DiaryListAction) -> Unit) {
        val focus = remember { FocusRequester() }
        val inputMode = LocalInputModeManager.current
        LaunchedEffect(Unit) { inputMode.requestInputMode(InputMode.Keyboard) }
        IFTheme {
            Column {
                IFHeader({}, periodControl = {
                    GradesPeriodControl(state, { onAction(DiaryListAction.ShowPeriods) }, Modifier.focusRequester(focus))
                })
                DiaryListContent(state, onAction, Modifier.weight(1f), periodFocus = focus)
            }
        }
    }

    @Test
    fun periodPickerExposesSelectionAndChangesHeaderAfterClosing() {
        var state by mutableStateOf(PreviewGrades)
        compose.setContent {
            Host(state) { action ->
                state = when (action) {
                    DiaryListAction.ShowPeriods -> state.copy(sheet = GradesSheet.PeriodPicker)
                    is DiaryListAction.SelectPeriod -> state.copy(selectedPeriod = action.period, sheet = GradesSheet.Closed)
                    DiaryListAction.CloseSheet -> state.copy(sheet = GradesSheet.Closed)
                    else -> state
                }
            }
        }
        compose.onNodeWithContentDescription(label(R.string.choose_academic_period)).performClick()
        compose.onAllNodesWithText("2026/2")
            .filterToOne(isSelected() and hasAnyAncestor(isDialog())).assertIsSelected()
        compose.onNodeWithText("2026/1").performClick()
        waitForClosedSheet()
        compose.runOnIdle { assertEquals(1, state.selectedPeriod!!.number) }
        compose.onNodeWithContentDescription(label(R.string.choose_academic_period)).assertTextContains("2026/1")
        compose.waitUntil(5_000) {
            compose.onNodeWithContentDescription(label(R.string.choose_academic_period)).fetchSemanticsNode()
                .config.getOrElse(SemanticsProperties.Focused) { false }
        }
    }

    @Test
    fun subjectSheetRetainsListPositionAndReturnsFocusToItsRow() {
        val subjects = (1..18).map { PreviewSubject.copy(id = it, name = "Subject $it") }
        var state by mutableStateOf(PreviewGrades.copy(diaries = subjects))
        compose.setContent {
            Host(state) { action ->
                state = when (action) {
                    is DiaryListAction.ShowSubject -> state.copy(sheet = GradesSheet.Subject(action.diaryId))
                    DiaryListAction.CloseSheet -> state.copy(sheet = GradesSheet.Closed)
                    else -> state
                }
            }
        }
        compose.onNode(hasScrollAction() and !hasAnyAncestor(isDialog()))
            .performScrollToNode(hasText("Subject 18"))
        val opener = compose.onNodeWithText("Subject 18")
        val before = opener.fetchSemanticsNode().boundsInRoot.top
        opener.performClick()
        compose.onNodeWithText(label(R.string.attendance)).assertIsDisplayed()
        Espresso.pressBack()
        waitForClosedSheet()
        opener.assertIsDisplayed()
        assertEquals(before, opener.fetchSemanticsNode().boundsInRoot.top, 2f)
        compose.waitUntil(5_000) {
            opener.fetchSemanticsNode().config.getOrElse(SemanticsProperties.Focused) { false }
        }
    }

    @Test
    fun returningFromAnotherPageRestoresPositionAndChangingPeriodResetsIt() {
        val subjects = (1..18).map { PreviewSubject.copy(id = it, name = "Subject $it") }
        var state by mutableStateOf(PreviewGrades.copy(diaries = subjects))
        var showingGrades by mutableStateOf(true)
        compose.setContent {
            val holder = rememberSaveableStateHolder()
            if (showingGrades) {
                holder.SaveableStateProvider("grades") { Host(state) {} }
            } else {
                Text("Profile")
            }
        }
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Subject 18"))
        val before = compose.onNodeWithText("Subject 18").fetchSemanticsNode().boundsInRoot.top
        compose.runOnIdle { showingGrades = false }
        compose.onNodeWithText("Profile").assertIsDisplayed()
        compose.runOnIdle { showingGrades = true }
        compose.onNodeWithText("Subject 18").assertIsDisplayed()
        assertEquals(before, compose.onNodeWithText("Subject 18").fetchSemanticsNode().boundsInRoot.top, 2f)
        compose.runOnIdle { state = state.copy(selectedPeriod = state.periods[1]) }
        compose.onNodeWithText("Subject 1").assertIsDisplayed()
    }

    @Test
    fun assessmentFailureKeepsOtherDetailsAndRetryIsLocal() {
        var action: DiaryListAction? = null
        compose.setContent {
            Host(PreviewGrades.copy(
                sheet = GradesSheet.Subject(1),
                assessments = mapOf(1 to SubjectAssessments(isLoading = false, error = IOError))
            )) { action = it }
        }
        compose.onNodeWithText(label(R.string.attendance)).assertIsDisplayed()
        compose.onNodeWithText(label(R.string.class_progress)).assertDoesNotExist()
        compose.onNodeWithText(label(R.string.retry)).performScrollTo().performClick()
        compose.runOnIdle { assertEquals(DiaryListAction.RetryAssessments(1), action) }
        compose.onNodeWithText(label(R.string.attendance)).assertExists()
        compose.onNodeWithText(label(R.string.total_hours)).assertDoesNotExist()
    }

    @Test
    fun zeroGradeAndMissingResultDateAndFormulaRemainDistinct() {
        compose.setContent { Host(PreviewGrades.copy(sheet = GradesSheet.Subject(1))) {} }
        compose.onNodeWithText("0").performScrollTo().assertIsDisplayed()
        compose.onAllNodesWithText(label(R.string.not_graded)).assertCountEquals(2)
        compose.onAllNodesWithText(label(R.string.date_not_supplied)).assertCountEquals(3)
        compose.onNodeWithText("(A1 + A2) / 2").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun unknownCountsShowOnlyHoursAndEmptyPeriodsHaveAnExplicitMessage() {
        var state by mutableStateOf(PreviewGrades.copy(assessments = emptyMap()))
        compose.setContent { Host(state) {} }
        compose.onNodeWithText(context.getString(R.string.hours_value, 80)).assertIsDisplayed()
        compose.runOnIdle {
            state = DiaryListState(isLoading = false)
        }
        compose.onNodeWithText(label(R.string.no_academic_periods)).assertIsDisplayed()
        compose.onNodeWithContentDescription(label(R.string.choose_academic_period)).assertIsNotEnabled()
    }

    private fun waitForClosedSheet() {
        compose.waitUntil(5_000) { compose.onAllNodes(isDialog()).fetchSemanticsNodes().isEmpty() }
    }
}
