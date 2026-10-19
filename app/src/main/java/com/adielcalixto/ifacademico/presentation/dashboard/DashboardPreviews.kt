package com.adielcalixto.ifacademico.presentation.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adielcalixto.ifacademico.domain.entities.*
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.IFBottomNavigation
import com.adielcalixto.ifacademico.presentation.components.IFCatalogPreviews
import com.adielcalixto.ifacademico.presentation.components.IFHeader
import com.adielcalixto.ifacademico.presentation.components.IFIcon
import com.adielcalixto.ifacademico.presentation.components.IFNavigationItem

internal val PreviewCourse = TimeTableClass(
    1, "Algoritmos e pensamento computacional", "Professora Maria Costa", 2,
    "08:00", "09:40", "Bloco A · Sala 204", true, 2
)
internal val PreviewDashboard = DashboardState(
    timeTable = TimeTable(
        listOf(PreviewCourse, PreviewCourse.copy(id = 2, className = "Banco de dados", startTime = "10:00", endTime = "11:40", finished = false)),
        "2026-10-19", "2026-10-19T09:41:00-03:00"
    ),
    performanceCoefficients = PerformanceCoefficients(8.72f, 7.94f, 9.3f),
    currentPeriod = Period(2026, 2),
    individualTimeTable = IndividualTimeTable(mapOf(2 to listOf(PreviewCourse)), 2),
    isLoading = false
)

@IFCatalogPreviews
@Composable
internal fun DashboardPreview() {
    IFTheme {
        Scaffold(
            topBar = { IFHeader({}) },
            bottomBar = {
                IFBottomNavigation(listOf(
                    IFNavigationItem("Hoje", IFIcon.Home), IFNavigationItem("Notas", IFIcon.Grades),
                    IFNavigationItem("Calendário", IFIcon.Calendar)
                ), 0, {})
            }
        ) { padding -> DashboardContent(PreviewDashboard, {}, Modifier.padding(padding)) }
    }
}

@IFCatalogPreviews
@Composable
internal fun TimetablePreview() {
    IFTheme { DashboardContent(PreviewDashboard.copy(sheet = DashboardSheet.Timetable), {}) }
}

@IFCatalogPreviews
@Composable
internal fun ClassDetailPreview() {
    IFTheme { DashboardContent(PreviewDashboard.copy(sheet = DashboardSheet.ClassDetail(PreviewCourse, true)), {}) }
}
