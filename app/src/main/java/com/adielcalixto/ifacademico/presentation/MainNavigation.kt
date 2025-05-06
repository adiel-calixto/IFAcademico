package com.adielcalixto.ifacademico.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.adielcalixto.ifacademico.domain.entities.Student
import com.adielcalixto.ifacademico.presentation.calendar.CalendarScreen
import com.adielcalixto.ifacademico.presentation.calendar.CalendarViewModel
import com.adielcalixto.ifacademico.presentation.components.BottomNavigationBar
import com.adielcalixto.ifacademico.presentation.components.TopBar
import com.adielcalixto.ifacademico.presentation.dashboard.DashboardScreen
import com.adielcalixto.ifacademico.presentation.dashboard.DashboardViewModel
import com.adielcalixto.ifacademico.presentation.diary_list.DiaryListScreen
import com.adielcalixto.ifacademico.presentation.diary_list.DiaryListViewModel
import com.adielcalixto.ifacademico.presentation.student_info.StudentInfoScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object StudentInfo : Screen()

    @Serializable
    data object Calendar : Screen()

    @Serializable
    data object Dashboard : Screen()

    @Serializable
    data object DiaryList : Screen()
}

@Composable
fun MainNavigation(
    navController: NavHostController,
    student: Student,
    onLogout: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding(),
        bottomBar = {
            BottomNavigationBar(navController)
        },
        topBar = {
            TopBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Dashboard> {
                val dashboardViewModel = hiltViewModel<DashboardViewModel>()
                DashboardScreen(dashboardViewModel)
            }

            composable<Screen.StudentInfo> {
                StudentInfoScreen(
                    student,
                    onLogout = onLogout
                )
            }

            composable<Screen.Calendar> {
                val calendarViewModel = hiltViewModel<CalendarViewModel>()
                CalendarScreen(calendarViewModel)
            }

            composable<Screen.DiaryList> {
                val diariesViewModel = hiltViewModel<DiaryListViewModel>()
                diariesViewModel.setRegistrationId(student.registrationId)

                DiaryListScreen(diariesViewModel)
            }
        }
    }
}