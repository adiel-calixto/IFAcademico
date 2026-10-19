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

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: IFIcon)

@Composable
fun getTopLevelRoutes() = listOf(
    TopLevelRoute(UiText.StringResource(R.string.today_label).asString(), Screen.Dashboard, IFIcon.Home),
    TopLevelRoute(UiText.StringResource(R.string.grades_label).asString(), Screen.DiaryList, IFIcon.Grades),
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
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
    )
}
