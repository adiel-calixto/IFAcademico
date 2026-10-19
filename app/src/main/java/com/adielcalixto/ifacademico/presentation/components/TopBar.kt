package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.adielcalixto.ifacademico.Screen
import com.adielcalixto.ifacademico.presentation.designsystem.IFHeader

@Composable
fun TopBar(navController: NavController) {
    IFHeader(onProfile = {
        navController.navigate(Screen.StudentInfo) {
            popUpTo(navController.graph.findStartDestination().id)
            launchSingleTop = true
            restoreState = false
        }
    })
}
