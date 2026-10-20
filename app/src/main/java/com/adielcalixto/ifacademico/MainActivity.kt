package com.adielcalixto.ifacademico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adielcalixto.ifacademico.domain.usecases.SessionState
import com.adielcalixto.ifacademico.presentation.AppTheme
import com.adielcalixto.ifacademico.presentation.MainTab
import com.adielcalixto.ifacademico.presentation.MainViewModel
import com.adielcalixto.ifacademico.presentation.SignedInShell
import com.adielcalixto.ifacademico.presentation.calendar.CalendarScreen
import com.adielcalixto.ifacademico.presentation.calendar.CalendarViewModel
import com.adielcalixto.ifacademico.presentation.components.ErrorComponent
import com.adielcalixto.ifacademico.presentation.components.LoadingComponent
import com.adielcalixto.ifacademico.presentation.dashboard.DashboardScreen
import com.adielcalixto.ifacademico.presentation.dashboard.DashboardViewModel
import com.adielcalixto.ifacademico.presentation.diary_list.DiaryListAction
import com.adielcalixto.ifacademico.presentation.diary_list.DiaryListScreen
import com.adielcalixto.ifacademico.presentation.diary_list.DiaryListViewModel
import com.adielcalixto.ifacademico.presentation.diary_list.GradesPeriodControl
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
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxHeight(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background,
                ) {
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
    data object SignedIn : Screen()

    @Serializable
    data object Tabs : Screen()
}

@Composable
fun App(
    viewModel: MainViewModel = viewModel(),
    updateViewModel: UpdateViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val state by viewModel.state.collectAsState()
    val updateState by updateViewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
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

    // Session invalidation also removes the shell's pager and session-scoped view models.
    val currentEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(state.sessionState, currentEntry) {
        if (state.sessionState == SessionState.Invalid &&
            currentEntry?.destination?.hasRoute<Screen.SignedIn>() == true
        ) {
            navController.navigate(Screen.Login) { popUpTo(0) }
        } else if (state.sessionState == SessionState.Valid &&
            currentEntry?.destination?.hasRoute<Screen.Login>() == true
        ) {
            navController.navigate(Screen.SignedIn) { popUpTo(0) }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash,
        modifier = Modifier.fillMaxSize().navigationBarsPadding().statusBarsPadding(),
    ) {
        composable<Screen.Splash> {
            LaunchedEffect(state.sessionState) {
                when (state.sessionState) {
                    SessionState.Invalid -> navController.navigate(Screen.Login) { popUpTo(0) }
                    SessionState.Valid -> navController.navigate(Screen.SignedIn) { popUpTo(0) }
                    else -> Unit
                }
            }
            LoadingComponent()
        }
        composable<Screen.Login> {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                loginViewModel,
                onLoginSuccess = viewModel::onLogin,
            )
        }
        composable<Screen.SignedIn> { shellEntry ->
            SignedInShell(
                periodControl = { focus ->
                    val model: DiaryListViewModel = hiltViewModel(shellEntry)
                    val gradesState by model.state.collectAsState()
                    GradesPeriodControl(
                        gradesState,
                        { model.onAction(DiaryListAction.ShowPeriods) },
                        Modifier.focusRequester(focus),
                    )
                },
                pageContent = { tab, periodFocus ->
                    when (tab) {
                        MainTab.Today -> DashboardScreen(hiltViewModel<DashboardViewModel>(shellEntry))
                        MainTab.Calendar -> CalendarScreen(hiltViewModel<CalendarViewModel>(shellEntry))
                        MainTab.Diaries -> {
                            val model: DiaryListViewModel = hiltViewModel(shellEntry)
                            LaunchedEffect(state.student?.registrationId) {
                                state.student?.registrationId?.let(model::setRegistrationId)
                            }
                            DiaryListScreen(model, periodFocus)
                        }
                    }
                },
                profileContent = {
                    val student = state.student
                    if (student == null) {
                        LoadingComponent()
                    } else {
                        StudentInfoScreen(
                            student = student,
                            isUpdateCheckEnabled = updateState.isUpdateCheckEnabled,
                            onUpdateCheckEnabledChange = updateViewModel::setUpdateCheckEnabled,
                            onLogout = {
                                viewModel.onLogout()
                                navController.navigate(Screen.Login) { popUpTo(0) }
                            },
                        )
                    }
                },
            )
        }
    }
}
