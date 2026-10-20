package com.adielcalixto.ifacademico.presentation.dashboard

import androidx.compose.runtime.*
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFTheme
import java.time.DayOfWeek
import java.time.format.TextStyle
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class DashboardInteractionTest {
    @get:Rule
    val compose = createComposeRule()
    private fun label(id: Int) =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    @Test
    fun homePlacesCoefficientBeforeClassesAndRowsOpenDetails() {
        var action: DashboardAction? = null
        compose.setContent { IFTheme { DashboardContent(PreviewDashboard, { action = it }) } }
        val coefficient = compose.onNodeWithText(label(R.string.student_coefficient))
            .fetchSemanticsNode().boundsInRoot
        val classes =
            compose.onNodeWithText(label(R.string.today_classes)).fetchSemanticsNode().boundsInRoot
        assertTrue(coefficient.top < classes.top)
        compose.onNodeWithText(PreviewCourse.className).performClick()
        compose.runOnIdle { assertEquals(DashboardAction.ShowClass(PreviewCourse), action) }
    }

    @Test
    fun emptyDayKeepsTimetableActionAvailable() {
        var action: DashboardAction? = null
        compose.setContent {
            IFTheme {
                DashboardContent(
                    PreviewDashboard.copy(
                        timeTable = PreviewDashboard.timeTable.copy(
                            classes = emptyList()
                        )
                    ), { action = it })
            }
        }
        compose.onNodeWithText(label(R.string.no_classes_today)).assertIsDisplayed()
        compose.onNodeWithText(label(R.string.timetable)).performClick()
        compose.runOnIdle { assertEquals(DashboardAction.ShowIndividualTimeTable, action) }
    }

    @Test
    fun weeklySelectionExposesStateAndUsesSundayFirstApiNumbering() {
        var action: DashboardAction? = null
        compose.setContent {
            IFTheme {
                DashboardContent(
                    PreviewDashboard.copy(sheet = DashboardSheet.Timetable),
                    { action = it })
            }
        }
        val locale =
            InstrumentationRegistry.getInstrumentation().targetContext.resources.configuration.locales[0]
        compose.onNodeWithText(DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT, locale))
            .assertIsSelected()
        compose.onNodeWithText(DayOfWeek.SUNDAY.getDisplayName(TextStyle.SHORT, locale))
            .performClick()
        compose.runOnIdle { assertEquals(DashboardAction.SelectWeekday(1), action) }
    }

    @Test
    fun weeklyRowsHaveNoDetailAction() {
        compose.setContent {
            IFTheme {
                DashboardContent(
                    PreviewDashboard.copy(sheet = DashboardSheet.Timetable),
                    {})
            }
        }
        compose.onAllNodesWithText(PreviewCourse.className)
            .filterToOne(hasAnyAncestor(isDialog())).assertHasNoClickAction()
        compose.onNodeWithText(label(R.string.starts_at)).assertDoesNotExist()
    }

    @Test
    fun closingTimetableReturnsFocusToHomeOpener() {
        var state by mutableStateOf(PreviewDashboard)
        compose.setContent {
            val inputMode = LocalInputModeManager.current
            LaunchedEffect(Unit) { inputMode.requestInputMode(InputMode.Keyboard) }
            IFTheme {
                DashboardContent(state, { action ->
                    when (action) {
                        DashboardAction.ShowIndividualTimeTable -> state =
                            state.copy(sheet = DashboardSheet.Timetable)

                        DashboardAction.HideIndividualTimeTable -> state =
                            state.copy(sheet = DashboardSheet.Closed)

                        else -> Unit
                    }
                })
            }
        }
        compose.onNodeWithText(label(R.string.timetable)).performClick()
        compose.onNodeWithText(label(R.string.weekly_schedule)).assertIsDisplayed()
        compose.onNodeWithContentDescription(label(R.string.close)).performClick()
        compose.waitUntil(5_000) { compose.onAllNodes(isDialog()).fetchSemanticsNodes().isEmpty() }
        compose.waitUntil(5_000) {
            compose.onNodeWithText(label(R.string.timetable)).fetchSemanticsNode()
                .config.getOrElse(androidx.compose.ui.semantics.SemanticsProperties.Focused) { false }
        }
    }

    @Test
    fun coefficientFailureReplacesItsPanelAndLeavesClassesVisible() {
        var action: DashboardAction? = null
        compose.setContent {
            IFTheme {
                DashboardContent(
                    PreviewDashboard.copy(coefficientsError = com.adielcalixto.ifacademico.domain.IOError),
                    { action = it })
            }
        }
        compose.onNodeWithText(label(R.string.coefficients_load_error)).assertIsDisplayed()
        compose.onNodeWithText(label(R.string.class_coefficient)).assertDoesNotExist()
        compose.onNodeWithText(PreviewCourse.className).assertExists()
        compose.onNodeWithText(label(R.string.retry)).performClick()
        compose.runOnIdle { assertEquals(DashboardAction.RetryCoefficients, action) }
    }

    @Test
    fun classFailureReplacesRowsAndKeepsTheCoefficientAndTimetableAction() {
        var action: DashboardAction? = null
        compose.setContent {
            IFTheme {
                DashboardContent(
                    PreviewDashboard.copy(classesError = com.adielcalixto.ifacademico.domain.IOError),
                    { action = it })
            }
        }
        compose.onNodeWithText(label(R.string.student_coefficient)).assertIsDisplayed()
        compose.onNodeWithText(PreviewCourse.className).assertDoesNotExist()
        compose.onNodeWithText(label(R.string.timetable)).assertIsEnabled()
        compose.onNodeWithText(label(R.string.retry)).performClick()
        compose.runOnIdle { assertEquals(DashboardAction.RetryClasses, action) }
    }

    @Test
    fun sessionFailureHasOnePagePlaceholderInsteadOfDuplicateSectionErrors() {
        compose.setContent {
            IFTheme {
                DashboardContent(
                    PreviewDashboard,
                    {},
                    sessionError = com.adielcalixto.ifacademico.domain.IOError
                )
            }
        }
        compose.onAllNodesWithText(label(R.string.content_load_error)).assertCountEquals(1)
        compose.onAllNodesWithText(label(R.string.retry)).assertCountEquals(1)
        compose.onNodeWithText(PreviewCourse.className).assertDoesNotExist()
        compose.onNodeWithText(label(R.string.student_coefficient)).assertDoesNotExist()
    }

    @Test
    fun timetableFailureReplacesPreviouslyLoadedScheduleAndKeepsDismissalAvailable() {
        compose.setContent {
            IFTheme {
                DashboardContent(
                    PreviewDashboard.copy(
                        sheet = DashboardSheet.Timetable,
                        timetableError = com.adielcalixto.ifacademico.domain.IOError
                    ), {})
            }
        }
        compose.onNodeWithContentDescription(label(R.string.close)).assertIsEnabled()
        compose.onAllNodesWithText(PreviewCourse.className)
            .filter(hasAnyAncestor(isDialog())).assertCountEquals(0)
        compose.onNode(hasText(label(R.string.retry)) and hasAnyAncestor(isDialog()))
            .assertHasClickAction()
    }

    @Test
    fun sessionRetryReplacesTheErrorWithLoadingBeforeRecoveryFinishes() {
        var state by mutableStateOf(PreviewDashboard)
        compose.setContent {
            IFTheme {
                DashboardContent(state, { action ->
                    if (action == DashboardAction.RetryHome) state =
                        state.copy(coefficientsLoading = true, classesLoading = true)
                }, sessionError = com.adielcalixto.ifacademico.domain.IOError)
            }
        }
        compose.onNodeWithText(label(R.string.retry)).performScrollTo().performClick()
        compose.onNodeWithText(label(R.string.retry)).assertDoesNotExist()
        compose.onNodeWithContentDescription(label(R.string.loading_coefficients)).assertExists()
        compose.onNodeWithContentDescription(label(R.string.loading_classes)).assertExists()
    }
}
