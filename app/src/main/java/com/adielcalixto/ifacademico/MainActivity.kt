package com.adielcalixto.ifacademico

import android.os.Bundle
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.adielcalixto.ifacademico.presentation.diary_list.DiaryListAction
import com.adielcalixto.ifacademico.presentation.diary_list.GradesPeriodControl
import com.adielcalixto.ifacademico.presentation.diary_list.DiaryListScreen
import com.adielcalixto.ifacademico.presentation.diary_list.DiaryListViewModel
import com.adielcalixto.ifacademico.presentation.login.LoginScreen
import com.adielcalixto.ifacademico.presentation.login.LoginViewModel
import com.adielcalixto.ifacademico.presentation.student_info.StudentInfoScreen
import com.adielcalixto.ifacademico.presentation.update.UpdateDialog
import com.adielcalixto.ifacademico.presentation.update.UpdateViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme() {
                Surface(modifier = Modifier.fillMaxHeight(), color = androidx.compose.material3.MaterialTheme.colorScheme.background) {
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
    viewModel: MainViewModel = viewModel(),
    updateViewModel: UpdateViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val state by viewModel.state.collectAsState()
    val updateState by updateViewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentEntry by navController.currentBackStackEntryAsState()
    val gradesEntry = currentEntry?.takeIf { it.destination.hasRoute<Screen.DiaryList>() }
    val gradesViewModel = gradesEntry?.let { hiltViewModel<DiaryListViewModel>(it) }
    val periodFocus = remember(gradesEntry) { FocusRequester() }

    LaunchedEffect(gradesViewModel, state.student?.registrationId) {
        state.student?.registrationId?.let { gradesViewModel?.setRegistrationId(it) }
    }

    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(viewModel)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    LaunchedEffect(Unit) {
        updateViewModel.checkForUpdates()
    }

    if (state.error != null) {
        ErrorComponent(
            error = state.error!!,
            onRetryClicked = viewModel::retryLoadingData
        )
        return
    }

    updateState.pendingUpdate?.let { info ->
        UpdateDialog(
            info = info,
            onSkipClick = updateViewModel::skipVersion,
        )
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
            if (state.isLoggedIn()) {
                val periodControl: (@Composable () -> Unit)? = gradesViewModel?.let { model ->
                    {
                        val gradesState by model.state.collectAsState()
                        GradesPeriodControl(gradesState, { model.onAction(DiaryListAction.ShowPeriods) },
                            Modifier.focusRequester(periodFocus))
                    }
                }
                TopBar(navController, periodControl)
            }
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
                val loginViewModel: LoginViewModel = hiltViewModel()
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
                    student = state.student!!,
                    isUpdateCheckEnabled = updateState.isUpdateCheckEnabled,
                    onUpdateCheckEnabledChange = updateViewModel::setUpdateCheckEnabled,
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
                DiaryListScreen(diariesViewModel, periodFocus)
            }
        }
    }
}
