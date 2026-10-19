package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.adielcalixto.ifacademico.Screen

@Composable
fun TopBar(navController: NavController) {
    IFHeader(onProfile = {
        navController.navigate(Screen.StudentInfo) {
            launchSingleTop = true
        }
    })
}
