package com.adielcalixto.ifacademico.presentation.diary_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.diary_detail.components.IFAssessmentRecord
import com.adielcalixto.ifacademico.presentation.diary_detail.components.IFAssessmentStage
import com.adielcalixto.ifacademico.presentation.diary_detail.components.IFAssessments
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AssessmentsInteractionTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val first = IFAssessmentRecord("Written assessment", "A1", "21/10/2026", "0", "20")
    private val second = IFAssessmentRecord("Calculated result", "M1", null, null, "10", "A1 / 2")

    private fun row(description: String) = compose.onNode(hasText(description) and hasClickAction())

    private fun host(fontScale: Float = 1f, dark: Boolean = false) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                IFTheme(darkTheme = dark) {
                    Column(Modifier.width(320.dp).verticalScroll(rememberScrollState())) {
                        IFAssessments(listOf(IFAssessmentStage("Stage 1", listOf(first, second))))
                    }
                }
            }
        }
    }

    @Test
    fun collapsedRowsShowLabeledGradeAndExposeExpansionState() {
        host()
        row(first.description)
            .assertHeightIsAtLeast(48.dp)
            .assert(SemanticsMatcher.expectValue(
                SemanticsProperties.StateDescription, context.getString(R.string.assessment_collapsed)
            ))
            .assert(hasText(context.getString(R.string.assessment_score, "0")))
        row(second.description).assert(hasText(context.getString(R.string.not_graded)))
        compose.onNodeWithText(context.getString(R.string.maximum_score, "20")).assertDoesNotExist()
        compose.onNodeWithText(context.getString(R.string.assessment_date, first.date)).assertDoesNotExist()
        compose.onNodeWithText("A1 / 2").assertDoesNotExist()
    }

    @Test
    fun rowsExpandIndependentlyAndKeepSuppliedValues() {
        host()
        row(first.description).performClick()
        row(first.description)
            .assert(SemanticsMatcher.expectValue(
                SemanticsProperties.StateDescription, context.getString(R.string.assessment_expanded)
            ))
            .assert(hasText(context.getString(R.string.maximum_score, "20")))
            .assert(hasText(context.getString(R.string.assessment_acronym, "A1")))
            .assert(hasText(context.getString(R.string.assessment_date, first.date)))
        row(second.description).performScrollTo().performClick()
        row(second.description)
            .assert(hasText(context.getString(R.string.maximum_score, "10")))
            .assert(hasText(context.getString(
                R.string.assessment_date, context.getString(R.string.date_not_supplied)
            )))
            .assert(hasText("A1 / 2"))
        row(first.description).assert(hasText(context.getString(R.string.maximum_score, "20")))
        row(first.description).performScrollTo().performClick()
        compose.onNodeWithText(context.getString(R.string.maximum_score, "20")).assertDoesNotExist()
        row(second.description).assert(hasText("A1 / 2"))
    }

    @Test
    fun narrowEnlargedTextStacksGradeAndKeepsDetailsAccessibleInDarkMode() {
        host(fontScale = 2f, dark = true)
        val name = compose.onNodeWithText(first.description, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val score = compose.onNodeWithText(
            context.getString(R.string.assessment_score, "0"), useUnmergedTree = true
        ).fetchSemanticsNode().boundsInRoot
        assertTrue("Grade should sit below the full description", score.top >= name.bottom)
        row(second.description).performScrollTo().performClick()
        compose.onNodeWithText("A1 / 2").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun blankAcronymAndFormulaAreOmittedFromExpandedDetails() {
        compose.setContent {
            IFTheme {
                IFAssessments(listOf(IFAssessmentStage("Stage 1", listOf(
                    first.copy(acronym = "  ", formula = "  ")
                ))))
            }
        }
        row(first.description).performClick()
        row(first.description).assert(hasText(context.getString(R.string.maximum_score, "20")))
        compose.onNodeWithText(context.getString(R.string.formula)).assertDoesNotExist()
        compose.onNodeWithText(context.getString(R.string.assessment_acronym, "  ")).assertDoesNotExist()
    }
}
