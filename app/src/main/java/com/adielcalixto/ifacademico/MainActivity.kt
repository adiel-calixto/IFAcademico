package com.adielcalixto.ifacademico

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adielcalixto.ifacademico.domain.entities.Session
import com.adielcalixto.ifacademico.domain.usecases.SessionState
import com.adielcalixto.ifacademico.presentation.AppTheme
import com.adielcalixto.ifacademico.presentation.MainState
import com.adielcalixto.ifacademico.presentation.MainViewModel
import com.adielcalixto.ifacademico.presentation.calendar.CalendarScreen
import com.adielcalixto.ifacademico.presentation.calendar.CalendarViewModel
import com.adielcalixto.ifacademico.presentation.components.BottomNavigationBar
import com.adielcalixto.ifacademico.presentation.components.ErrorComponent
import com.adielcalixto.ifacademico.presentation.components.LoadingComponent
import com.adielcalixto.ifacademico.presentation.components.TopBar
import com.adielcalixto.ifacademico.presentation.dashboard.DashboardScreen
import com.adielcalixto.ifacademico.presentation.dashboard.DashboardViewModel
import com.adielcalixto.ifacademico.presentation.diary_list.DiaryListScreen
import com.adielcalixto.ifacademico.presentation.diary_list.DiaryListViewModel
import com.adielcalixto.ifacademico.presentation.login.LoginScreen
import com.adielcalixto.ifacademico.presentation.login.LoginViewModel
import com.adielcalixto.ifacademico.presentation.student_info.StudentInfoScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme() {
                Surface(modifier = Modifier.fillMaxHeight()) {
                    App()
                }
            }
        }
    }
}

@Serializable
sealed class Screen {
    @Serializable
    data object Login : Screen()

    @Serializable
    data object Splash : Screen()

    @Serializable
    data object StudentInfo : Screen()

    @Serializable
    data object Calendar : Screen()

    @Serializable
    data object Dashboard : Screen()

    @Serializable
    data object DiaryList : Screen()
}

private fun MainState.isLoggedIn() = this.sessionState == SessionState.Valid

@Composable
fun App(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val state by viewModel.state.collectAsState()

    if (state.error != null) {
        ErrorComponent(
            error = state.error!!,
            onRetryClicked = viewModel::retryLoadingData
        )
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding(),
        bottomBar = {
            if (state.isLoggedIn()) BottomNavigationBar(navController)
        },
        topBar = {
            if (state.isLoggedIn()) TopBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Splash> {
                LaunchedEffect(state.sessionState) {
                    when (state.sessionState) {
                        SessionState.Invalid -> {
                            navController.navigate(Screen.Login) { popUpTo(0) }
                        }

                        SessionState.Valid -> {
                            navController.navigate(Screen.Dashboard) { popUpTo(0) }
                        }

                        else -> {}
                    }
                }

                LoadingComponent()
            }

            composable<Screen.Login> {
                val loginViewModel = hiltViewModel<LoginViewModel>()
                LoginScreen(
                    loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard) { popUpTo(0) }
                        viewModel.onLogin()
                    })
            }

            composable<Screen.Dashboard> {
                val dashboardViewModel = hiltViewModel<DashboardViewModel>()
                DashboardScreen(dashboardViewModel)
            }

            composable<Screen.StudentInfo> {
                if (state.student == null) {
                    LoadingComponent()
                    return@composable
                }

                StudentInfoScreen(
                    state.student!!,
                    onLogout = {
                        viewModel.onLogout()
                        navController.navigate(Screen.Login) { popUpTo(0) }
                    }
                )
            }

            composable<Screen.Calendar> {
                val calendarViewModel = hiltViewModel<CalendarViewModel>()
                CalendarScreen(calendarViewModel)
            }

            composable<Screen.DiaryList> {
                if (state.student == null) {
                    LoadingComponent()
                    return@composable
                }

                val diariesViewModel = hiltViewModel<DiaryListViewModel>()
                diariesViewModel.setRegistrationId(state.student!!.registrationId)

                DiaryListScreen(diariesViewModel)
            }
        }
    }
}