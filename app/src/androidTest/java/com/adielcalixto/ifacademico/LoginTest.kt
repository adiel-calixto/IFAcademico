package com.adielcalixto.ifacademico

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.usecases.LoginUseCase
import com.adielcalixto.ifacademico.presentation.login.LoginScreen
import com.adielcalixto.ifacademico.presentation.login.LoginViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 2)
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var loginUseCase: LoginUseCase

    @Before
    fun setupAppNavHost() {
        hiltRule.inject()
    }

    @Test
    fun verifyLoginSuccess() {
        coEvery { loginUseCase.execute(any(), any()) } returns (Result.Success(Unit))

        var loginSuccess = false

        composeTestRule.activity.setContent {
            LoginScreen(LoginViewModel(loginUseCase), onLoginSuccess = { loginSuccess = true })
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.registration))
            .performTextInput("Test")

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.password))
            .performTextInput("Test")

        composeTestRule.onAllNodesWithText("Login")
            .onLast()
            .performClick()

        Assert.assertTrue(loginSuccess)
        coVerify { loginUseCase.execute(any(), any()) }
    }

    @Test
    fun verifyLoginError_whenFieldsAreEmpty() {
        coEvery { loginUseCase.execute(any(), any()) } returns (Result.Success(Unit))

        var loginSuccess = false

        composeTestRule.activity.setContent {
            LoginScreen(LoginViewModel(loginUseCase), onLoginSuccess = { loginSuccess = true })
        }

        composeTestRule.onAllNodesWithText("Login")
            .onLast()
            .performClick()

        Assert.assertFalse(loginSuccess)
    }

    @Test
    fun verifyLoginDisplaysError_whenResultIsError() {
        coEvery { loginUseCase.execute(any(), any()) } returns (Result.Error(UnknownError))

        var loginSuccess = false

        composeTestRule.activity.setContent {
            LoginScreen(LoginViewModel(loginUseCase), onLoginSuccess = { loginSuccess = true })
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.registration))
            .performTextInput("Test")

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.password))
            .performTextInput("Test")

        composeTestRule.onAllNodesWithText("Login")
            .onLast()
            .performClick()

        composeTestRule.onNode(
            isDialog()
                    and
                    hasAnyDescendant(
                        hasText(composeTestRule.activity.getString(R.string.error_ocurred))
                    ),
        )
            .assertIsDisplayed()

        Assert.assertFalse(loginSuccess)
        coVerify { loginUseCase.execute(any(), any()) }
    }
}