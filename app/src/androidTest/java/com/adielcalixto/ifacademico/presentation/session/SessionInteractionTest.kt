package com.adielcalixto.ifacademico.presentation.session

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.data.session.AppForegroundObserver
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.presentation.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SessionInteractionTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun sessionFailureReplacesItsRegionOnceAndPreservesTheSelectedPage() {
        var error by mutableStateOf<com.adielcalixto.ifacademico.domain.Error.DataError?>(null)
        var retries = 0
        compose.setContent {
            IFTheme(darkTheme = false) {
                SignedInShell(
                    periodControl = {},
                    pageContent = { tab, _ ->
                        val failure = LocalAcademicError.current
                        if (failure == null || !LocalAcademicVisible.current) Text("Academic content ${tab.name}")
                        else com.adielcalixto.ifacademico.presentation.components.IFErrorState(
                            failure.asUiText().asString(), { retries++; error = null })
                    },
                    profileContent = { Text("Student profile") },
                    sessionError = error,
                )
            }
        }
        val calendar = compose.activity.getString(R.string.calendar)
        compose.onNode(hasText(calendar) and hasClickAction()).performClick().assertIsSelected()
        compose.runOnIdle { error = IOError }
        compose.onNodeWithText("Academic content Calendar").assertDoesNotExist()
        compose.onAllNodesWithText(compose.activity.getString(R.string.content_load_error))
            .assertCountEquals(1)
        compose.onNodeWithText(compose.activity.getString(R.string.retry)).assertHasClickAction()
            .performClick()
        compose.onNode(hasText(calendar) and hasClickAction()).assertIsSelected()
        compose.onNodeWithText("Academic content Calendar").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test
    fun realActivityBackgroundReturnRefreshesButRecreationAndResumeDoNot() {
        var refreshes = 0
        val observer = AppForegroundObserver { refreshes++ }
        val application = compose.activity.application as Application
        compose.runOnIdle {
            application.registerActivityLifecycleCallbacks(observer)
            observer.onActivityStarted(compose.activity)
        }
        try {
            compose.activityRule.scenario.moveToState(Lifecycle.State.STARTED)
            compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
            compose.runOnIdle { assertEquals(0, refreshes) }
            compose.activityRule.scenario.recreate()
            compose.runOnIdle { assertEquals(0, refreshes) }
            compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
            compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
            compose.runOnIdle { assertEquals(1, refreshes) }
        } finally {
            application.unregisterActivityLifecycleCallbacks(observer)
        }
    }
}
