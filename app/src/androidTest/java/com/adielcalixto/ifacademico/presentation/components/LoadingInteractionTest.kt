package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.runtime.*
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.dashboard.*
import com.adielcalixto.ifacademico.presentation.diary_list.*
import com.adielcalixto.ifacademico.presentation.student_info.StudentInfoLoading
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LoadingInteractionTest {
    @get:Rule
    val compose = createComposeRule()
    private fun label(id: Int) =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    private val loading = SemanticsMatcher.expectValue(
        SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo.Indeterminate,
    )

    @Test
    fun sessionLoadingHasOneAccessibleStatusAndNoPageContent() {
        compose.setContent { IFTheme { IFSessionLoading() } }
        compose.onNodeWithText(label(R.string.checking_session)).assert(loading)
        compose.onAllNodes(loading).assertCountEquals(1)
        compose.onAllNodes(hasClickAction()).assertCountEquals(0)
        compose.onNodeWithText(label(R.string.today_classes)).assertDoesNotExist()
        compose.onNodeWithText(label(R.string.login)).assertDoesNotExist()
    }

    @Test
    fun homeLoadingKeepsHeadingsButDoesNotExposeStaleAcademicData() {
        var state by mutableStateOf(
            PreviewDashboard.copy(
                coefficientsLoading = true,
                classesLoading = true
            )
        )
        compose.setContent { IFTheme { DashboardContent(state, {}) } }
        compose.onNodeWithContentDescription(label(R.string.loading_coefficients)).assert(loading)
        compose.onNodeWithContentDescription(label(R.string.loading_classes)).assert(loading)
        compose.onAllNodes(loading).assertCountEquals(2)
        compose.onNodeWithText(label(R.string.student_coefficient)).assertExists()
        compose.onNodeWithText(label(R.string.today_classes)).assertExists()
        compose.onNodeWithText(label(R.string.timetable)).assertIsEnabled()
        compose.onNodeWithText(PreviewCourse.className).assertDoesNotExist()
        compose.runOnIdle {
            state = state.copy(coefficientsLoading = false, classesLoading = false)
        }
        compose.onAllNodes(loading).assertCountEquals(0)
        compose.onNodeWithText(PreviewCourse.className).assertExists()
        compose.onNodeWithText(label(R.string.timetable)).assertIsEnabled()
    }

    @Test
    fun subjectLoadingKeepsPeriodAndTransitionsThroughRetryToContent() {
        var state by mutableStateOf(PreviewGrades.copy(isLoading = true))
        var action: DiaryListAction? = null
        compose.setContent { IFTheme { DiaryListContent(state, { action = it }) } }
        compose.onNodeWithText("2026/2").assertExists()
        compose.onNodeWithContentDescription(label(R.string.loading_subjects)).assert(loading)
        compose.onAllNodes(hasClickAction()).assertCountEquals(0)
        compose.onNodeWithText(PreviewSubject.name).assertDoesNotExist()
        compose.runOnIdle { state = state.copy(isLoading = false, error = IOError) }
        compose.onAllNodes(loading).assertCountEquals(0)
        compose.onNodeWithText(label(R.string.retry)).performClick()
        compose.runOnIdle {
            assertEquals(DiaryListAction.LoadViewModelData, action)
            state = PreviewGrades
        }
        compose.onNodeWithText(PreviewSubject.name).assertExists()
    }

    @Test
    fun assessmentLoadingLeavesAttendanceAvailableAndHasLocalFailure() {
        var state by mutableStateOf(
            PreviewGrades.copy(
                sheet = GradesSheet.Subject(1), assessments = mapOf(1 to SubjectAssessments()),
            )
        )
        compose.setContent { IFTheme { DiaryListContent(state, {}) } }
        compose.onNodeWithText(label(R.string.attendance)).assertExists()
        compose.onNodeWithContentDescription(label(R.string.loading_assessments)).assert(loading)
        compose.onAllNodes(loading).assertCountEquals(1)
        compose.runOnIdle {
            state = state.copy(
                assessments = mapOf(
                    1 to SubjectAssessments(
                        isLoading = false,
                        error = IOError
                    )
                )
            )
        }
        compose.onAllNodes(loading).assertCountEquals(0)
        compose.onNodeWithText(label(R.string.attendance)).assertExists()
        compose.onNodeWithText(label(R.string.retry)).assertExists()
    }

    @Test
    fun timetableCanBeDismissedWhileLoading() {
        var state by mutableStateOf(
            PreviewDashboard.copy(
                sheet = DashboardSheet.Timetable, timetableLoading = true,
            )
        )
        compose.setContent {
            IFTheme {
                DashboardContent(state, { action ->
                    if (action == DashboardAction.HideIndividualTimeTable) {
                        state = state.copy(sheet = DashboardSheet.Closed, timetableLoading = false)
                    }
                })
            }
        }
        compose.onNodeWithContentDescription(label(R.string.loading_timetable)).assert(loading)
        compose.onNodeWithContentDescription(label(R.string.close)).performClick()
        compose.waitUntil(5_000) { compose.onAllNodes(isDialog()).fetchSemanticsNodes().isEmpty() }
        compose.onAllNodes(loading).assertCountEquals(0)
    }

    @Test
    fun profileLoadingHasNoFakeSettingsActions() {
        compose.setContent { IFTheme { StudentInfoLoading() } }
        compose.onNodeWithContentDescription(label(R.string.loading_profile)).assert(loading)
        compose.onAllNodes(loading).assertCountEquals(1)
        compose.onNodeWithText(label(R.string.preferences)).assertExists()
        compose.onAllNodes(hasClickAction()).assertCountEquals(0)
    }
}
