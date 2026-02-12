package com.adielcalixto.ifacademico.presentation.dashboard

import com.adielcalixto.ifacademico.presentation.diary_list.DiaryListAction

sealed class DashboardAction {
    data object LoadViewModelData: DashboardAction()
    data object ShowIndividualTimeTable : DashboardAction()
}