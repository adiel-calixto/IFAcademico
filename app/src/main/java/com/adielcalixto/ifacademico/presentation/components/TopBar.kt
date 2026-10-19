package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.adielcalixto.ifacademico.Screen

@Composable
fun TopBar(navController: NavController, periodControl: (@Composable () -> Unit)? = null) {
    IFHeader(periodControl = periodControl, onProfile = {
        navController.navigate(Screen.StudentInfo) {
            launchSingleTop = true
        }
    })
}
