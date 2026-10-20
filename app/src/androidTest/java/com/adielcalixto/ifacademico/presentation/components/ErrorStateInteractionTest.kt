package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.presentation.IFTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ErrorStateInteractionTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun fullPageErrorKeepsRetryAccessibleAtDoubleTextSize() {
        var retries = 0
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                IFTheme(darkTheme = true) { ErrorComponent(IOError) { retries++ } }
            }
        }
        val retry =
            InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.retry)
        compose.onNodeWithText(retry).performScrollTo().assertIsDisplayed()
            .assertHeightIsAtLeast(48.dp).assertHasClickAction().performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }
}
