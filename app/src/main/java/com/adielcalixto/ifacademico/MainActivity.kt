package com.adielcalixto.ifacademico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.adielcalixto.ifacademico.domain.usecases.SessionState
import com.adielcalixto.ifacademico.presentation.AppTheme
import com.adielcalixto.ifacademico.presentation.MainNavigation
import com.adielcalixto.ifacademico.presentation.MainViewModel
import com.adielcalixto.ifacademico.presentation.components.ErrorComponent
import com.adielcalixto.ifacademico.presentation.components.LoadingComponent
import com.adielcalixto.ifacademico.presentation.login.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

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

@Composable
fun App(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> {
            LoadingComponent()
        }

        state.error != null -> {
            ErrorComponent(
                error = state.error!!,
                onRetryClicked = viewModel::retryLoadingData
            )
        }

        state.sessionState == SessionState.Valid -> {
            MainNavigation(
                navController = navController,
                student = state.student!!,
                onLogout = viewModel::onLogout
            )
        }

        else -> {
            LoginScreen(onLoginSuccess = viewModel::onLogin)
        }
    }
}