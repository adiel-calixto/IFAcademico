package com.adielcalixto.ifacademico.presentation.dashboard

sealed class DashboardAction {
    data object LoadViewModelData: DashboardAction()
}