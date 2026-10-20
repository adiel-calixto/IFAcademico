package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.MainTab

@Composable
fun BottomNavigationBar(selectedTab: MainTab?, onSelect: (MainTab) -> Unit) {
    IFBottomNavigation(
        items = listOf(
            IFNavigationItem(stringResource(R.string.today_label), IFIcon.Home),
            IFNavigationItem(stringResource(R.string.grades_label), IFIcon.Grades),
            IFNavigationItem(stringResource(R.string.calendar), IFIcon.Calendar),
        ),
        selectedIndex = selectedTab?.ordinal ?: -1,
        onSelect = { onSelect(MainTab.entries[it]) },
    )
}
