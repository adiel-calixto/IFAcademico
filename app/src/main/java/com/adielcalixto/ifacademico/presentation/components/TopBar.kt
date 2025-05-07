package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.adielcalixto.ifacademico.Screen

@Composable
fun TopBar(navController: NavController) {
    TopAppBar(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        title = {
            Row {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    shape = CircleShape,
                    onClick = {
                        navController.navigate(Screen.StudentInfo) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                ) {
                    Icon(Icons.Filled.PersonOutline, "Person", Modifier.size(24.dp))
                }
            }
        }
    )
}