package com.adielcalixto.ifacademico.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adielcalixto.ifacademico.Screen
import com.adielcalixto.ifacademico.presentation.components.BottomNavigationBar
import com.adielcalixto.ifacademico.presentation.components.TopBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Tab order is also the pager order; swipes never add entries to navigation history. */
enum class MainTab { Today, Diaries, Calendar }

@Composable
internal fun SignedInShell(
    periodControl: @Composable (FocusRequester) -> Unit,
    pageContent: @Composable (MainTab, FocusRequester) -> Unit,
    profileContent: @Composable () -> Unit,
) {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val showingProfile = entry?.destination?.hasRoute<Screen.StudentInfo>() == true
    // The pager survives the Tabs destination being saved while Profile is open.
    val pager = rememberPagerState { MainTab.entries.size }
    val periodFocus = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    var tabJob by remember { mutableStateOf<Job?>(null) }
    val travel = with(LocalDensity.current) { IFMotion.pageTravel.roundToPx() }
    val settledTab = MainTab.entries[pager.settledPage]

    fun selectTab(tab: MainTab) {
        tabJob?.cancel()
        if (navController.currentDestination?.hasRoute<Screen.StudentInfo>() == true) {
            navController.popBackStack()
        }
        tabJob = scope.launch {
            pager.animateScrollToPage(
                tab.ordinal,
                animationSpec = tween(IFMotion.tabMillis, easing = FastOutSlowInEasing),
            )
        }
    }

    BackHandler(
        enabled = !showingProfile && (pager.settledPage != 0 || pager.currentPage != 0),
    ) {
        selectTab(MainTab.Today)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                onProfile = {
                    if (navController.currentDestination?.hasRoute<Screen.StudentInfo>() != true) {
                        tabJob?.cancel()
                        tabJob = scope.launch {
                            // Never retain a half-dragged page underneath Profile.
                            pager.scrollToPage(pager.settledPage)
                            navController.navigate(Screen.StudentInfo) { launchSingleTop = true }
                        }
                    }
                },
                periodControl = if (!showingProfile && settledTab == MainTab.Diaries) {
                    { periodControl(periodFocus) }
                } else null,
            )
        },
        bottomBar = {
            BottomNavigationBar(if (showingProfile) null else settledTab, ::selectTab)
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Tabs,
            modifier = Modifier.padding(padding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            composable<Screen.Tabs>(
                exitTransition = {
                    fadeOut(tween(IFMotion.profileMillis)) +
                        slideOutHorizontally(tween(IFMotion.profileMillis)) { -travel }
                },
                popEnterTransition = {
                    fadeIn(tween(IFMotion.profileMillis)) +
                        slideInHorizontally(tween(IFMotion.profileMillis)) { -travel }
                },
            ) {
                HorizontalPager(
                    state = pager,
                    modifier = Modifier.fillMaxSize(),
                    key = { MainTab.entries[it].name },
                ) { index ->
                    pageContent(MainTab.entries[index], periodFocus)
                }
            }
            composable<Screen.StudentInfo>(
                enterTransition = {
                    fadeIn(tween(IFMotion.profileMillis)) +
                        slideInHorizontally(tween(IFMotion.profileMillis)) { travel }
                },
                popExitTransition = {
                    fadeOut(tween(IFMotion.profileMillis)) +
                        slideOutHorizontally(tween(IFMotion.profileMillis)) { travel }
                },
            ) {
                profileContent()
            }
        }
    }
}
