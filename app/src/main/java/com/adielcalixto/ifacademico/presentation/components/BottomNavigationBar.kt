package com.adielcalixto.ifacademico.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.Screen
import com.adielcalixto.ifacademico.presentation.UiText

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

@Composable
fun getTopLevelRoutes() = listOf(
    TopLevelRoute("Dashboard", Screen.Dashboard, Icons.Filled.Home),
    TopLevelRoute(
        UiText.StringResource(R.string.diaries).asString(),
        Screen.DiaryList,
        Icons.Filled.Book
    ),
    TopLevelRoute(
        UiText.StringResource(R.string.calendar).asString(),
        Screen.Calendar,
        Icons.Filled.CalendarMonth
    )
)

@SuppressLint("RestrictedApi")
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        getTopLevelRoutes().forEach { topLevelRoute ->
            BottomNavigationItem(
                modifier = Modifier.padding(4.dp),
                icon = {
                    Icon(
                        topLevelRoute.icon,
                        contentDescription = topLevelRoute.name,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        topLevelRoute.name,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                },
                selected = currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true,
                onClick = {
                    navController.navigate(topLevelRoute.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}