package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.Screen
import com.adielcalixto.ifacademico.presentation.UiText
import com.adielcalixto.ifacademico.presentation.designsystem.IFBottomNavigation
import com.adielcalixto.ifacademico.presentation.designsystem.IFIcon
import com.adielcalixto.ifacademico.presentation.designsystem.IFNavigationItem

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: IFIcon)

@Composable
fun getTopLevelRoutes() = listOf(
    TopLevelRoute("Dashboard", Screen.Dashboard, IFIcon.Home),
    TopLevelRoute(UiText.StringResource(R.string.diaries).asString(), Screen.DiaryList, IFIcon.Grades),
    TopLevelRoute(UiText.StringResource(R.string.calendar).asString(), Screen.Calendar, IFIcon.Calendar),
)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val routes = getTopLevelRoutes()
    IFBottomNavigation(
        items = routes.map { IFNavigationItem(it.name, it.icon) },
        selectedIndex = routes.indexOfFirst { route ->
            destination?.hierarchy?.any { it.hasRoute(route.route::class) } == true
        },
        onSelect = { index ->
            navController.navigate(routes[index].route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
                restoreState = true
            }
        },
    )
}
