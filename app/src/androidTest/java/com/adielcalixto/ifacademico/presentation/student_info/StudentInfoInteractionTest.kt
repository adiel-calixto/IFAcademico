package com.adielcalixto.ifacademico.presentation.student_info

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.Student
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class StudentInfoInteractionTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun cancelLogoutKeepsSessionAndReturnsFocus() {
        var loggedOut = false
        lateinit var logout: String
        lateinit var cancel: String
        compose.setContent {
            IFTheme {
                logout = stringResource(R.string.logout)
                cancel = stringResource(R.string.cancel)
                Surface {
                    StudentInfoContent(
                        student,
                        Theme.SYSTEM,
                        {},
                        true,
                        {},
                        { loggedOut = true },
                        {})
                }
            }
        }
        compose.onNodeWithText(logout).performScrollTo().performClick()
        compose.onNode(isDialog()).assertExists()
        compose.onNodeWithText(cancel).performClick()
        compose.waitForIdle()
        compose.onNode(isDialog()).assertDoesNotExist()
        compose.onNodeWithText(logout).assertIsFocused()
        compose.runOnIdle { assertFalse(loggedOut) }
    }

    @Test
    fun confirmedLogoutEndsSessionOnce() {
        var logoutCount = 0
        lateinit var logout: String
        compose.setContent {
            IFTheme {
                logout = stringResource(R.string.logout)
                Surface {
                    StudentInfoContent(student, Theme.SYSTEM, {}, false, {}, { logoutCount++ }, {})
                }
            }
        }
        compose.onNodeWithText(logout).performScrollTo().performClick()
        compose.onNode(hasText(logout) and hasClickAction() and hasAnyAncestor(isDialog()))
            .performClick()
        compose.onNode(isDialog()).assertDoesNotExist()
        compose.runOnIdle { assertEquals(1, logoutCount) }
    }

    @Test
    fun enlargedTextCanReachProjectAndChangeSettings() {
        var projectCount = 0
        lateinit var project: String
        lateinit var light: String
        lateinit var system: String
        compose.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, 2f)) {
                IFTheme {
                    var theme by remember { mutableStateOf(Theme.SYSTEM) }
                    var updates by remember { mutableStateOf(true) }
                    project = stringResource(R.string.view_on_github)
                    light = stringResource(R.string.light_theme)
                    system = stringResource(R.string.system_theme)
                    Surface(Modifier.height(480.dp)) {
                        StudentInfoContent(
                            student, theme, { theme = it }, updates, { updates = it }, {},
                            { projectCount++ },
                        )
                    }
                }
            }
        }
        compose.onNodeWithText("AM").performScrollTo().assertIsDisplayed()
        compose.onNode(isToggleable()).performScrollTo().assertIsOn().performClick().assertIsOff()
        compose.onNode(hasText(light, substring = true)).assertDoesNotExist()
        compose.onNode(hasText(system, substring = true) and hasClickAction()).performScrollTo()
            .performClick()
        compose.onNodeWithText(light).performClick()
        compose.onNode(hasText(light, substring = true)).assertExists()
        compose.onNodeWithText(project).performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, projectCount) }
    }


    @Test
    fun identityFailureReplacesOldIdentityAndKeepsPreferencesAndLogout() {
        var retried = false
        lateinit var retry: String
        lateinit var preferences: String
        lateinit var logout: String
        compose.setContent {
            IFTheme {
                retry = stringResource(R.string.retry)
                preferences = stringResource(R.string.preferences)
                logout = stringResource(R.string.logout)
                StudentInfoContent(
                    student,
                    Theme.SYSTEM,
                    {},
                    true,
                    {},
                    {},
                    {},
                    error = com.adielcalixto.ifacademico.domain.IOError,
                    onRetry = { retried = true })
            }
        }
        compose.onNodeWithText(student.name).assertDoesNotExist()
        compose.onNodeWithText(student.courseName).assertDoesNotExist()
        compose.onNodeWithText(retry).performScrollTo().performClick()
        compose.onNodeWithText(preferences).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(logout).performScrollTo().assertHasClickAction()
        compose.runOnIdle { org.junit.Assert.assertTrue(retried) }
    }

    private val student = Student("Ana Maria de Oliveira", 42, 1, "Ciência da Computação")
}
