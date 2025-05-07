package com.adielcalixto.ifacademico

import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import com.adielcalixto.ifacademico.domain.usecases.GetStudentUseCase
import com.adielcalixto.ifacademico.domain.usecases.LogoutUseCase
import com.adielcalixto.ifacademico.domain.usecases.RefreshSessionUseCase
import com.adielcalixto.ifacademico.domain.usecases.SessionState
import com.adielcalixto.ifacademico.domain.usecases.VerifySessionUseCase
import com.adielcalixto.ifacademico.observers.UnauthorizedApiErrorObserver
import com.adielcalixto.ifacademico.presentation.MainViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.delay
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class NavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    lateinit var navController: TestNavHostController

    @MockK
    lateinit var verifySessionUseCase: VerifySessionUseCase

    @Inject
    lateinit var getStudentUseCase: GetStudentUseCase

    @Inject
    lateinit var logoutUseCase: LogoutUseCase

    @Inject
    lateinit var refreshSessionUseCase: RefreshSessionUseCase

    @Inject
    lateinit var unauthorizedApiErrorObserver: UnauthorizedApiErrorObserver

    @Before
    fun setupDependencies() {
        hiltRule.inject()
    }

    @Test
    fun verifyLoginScreen_whenSessionStateInvalid() {
        coEvery { verifySessionUseCase.execute() } returns (SessionState.Invalid)

        composeTestRule.activity.setContent { MockedApp() }
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Login")
            .fetchSemanticsNodes()
            .isNotEmpty()

        coVerify { verifySessionUseCase.execute() }
    }

    @Test
    fun test_AppRestartsProperly() {
        coEvery { verifySessionUseCase.execute() } coAnswers { delay(1); SessionState.Valid }

        // Launch the app
        composeTestRule.activity.setContent { MockedApp() }
        composeTestRule.waitForIdle()

        // Simulate app close
        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        // Check if the correct screen is restored
        composeTestRule.onNodeWithText("Dashboard").assertExists()
    }

    @Test
    fun verifyDashboardScreen_whenSessionStateValid() {
        coEvery { verifySessionUseCase.execute() } returns (SessionState.Valid)

        composeTestRule.activity.setContent { MockedApp() }
        composeTestRule.waitForIdle()

        Assert.assertTrue(
            navController.currentBackStackEntry?.destination?.hasRoute<Screen.Dashboard>() ?: false
        )

        coVerify { verifySessionUseCase.execute() }
    }

    @Test
    fun bottomNavigation_navigateToScreens() {
        coEvery { verifySessionUseCase.execute() } returns (SessionState.Valid)

        composeTestRule.activity.setContent { MockedApp() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.diaries))
            .performClick()

        Assert.assertTrue(
            navController.currentBackStackEntry?.destination?.hasRoute<Screen.DiaryList>() ?: false
        )

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.calendar))
            .performClick()

        Assert.assertTrue(
            navController.currentBackStackEntry?.destination?.hasRoute<Screen.Calendar>() ?: false
        )

        composeTestRule.onNodeWithText("Dashboard")
            .performClick()

        Assert.assertTrue(
            navController.currentBackStackEntry?.destination?.hasRoute<Screen.Dashboard>() ?: false
        )
    }

    @Composable
    private fun MockedApp() {
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        App(
            navController = navController,
            viewModel = MainViewModel(
                logoutUseCase,
                verifySessionUseCase,
                getStudentUseCase,
                refreshSessionUseCase,
                unauthorizedApiErrorObserver
            )
        )
    }
}