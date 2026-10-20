package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.calendar.CalendarContent
import com.adielcalixto.ifacademico.presentation.calendar.components.PreviewCalendar
import com.adielcalixto.ifacademico.presentation.calendar.components.PreviewCalendarDate
import com.adielcalixto.ifacademico.presentation.dashboard.*
import com.adielcalixto.ifacademico.presentation.diary_list.*
import com.adielcalixto.ifacademico.presentation.student_info.StudentInfoLoading

@IFCatalogPreviews
@Composable
internal fun SessionLoadingPreview() {
    IFTheme {
        Surface(Modifier.fillMaxSize(), color = IFTheme.colors.background) { IFSessionLoading() }
    }
}

@IFCatalogPreviews
@Composable
internal fun HomeLoadingPreview() = LoadingPage(0) { modifier ->
    DashboardContent(DashboardState(), {}, modifier)
}

@IFCatalogPreviews
@Composable
internal fun GradesLoadingPreview() = LoadingPage(1) { modifier ->
    DiaryListContent(PreviewGrades.copy(isLoading = true, diaries = emptyList()), {}, modifier)
}

@IFCatalogPreviews
@Composable
internal fun CalendarLoadingPreview() = LoadingPage(2) { modifier ->
    CalendarContent(PreviewCalendar.copy(isLoading = true), {}, modifier, today = PreviewCalendarDate)
}

@IFCatalogPreviews
@Composable
internal fun TimetableLoadingPreview() = LoadingPage(0) { modifier ->
    DashboardContent(
        PreviewDashboard.copy(sheet = DashboardSheet.Timetable, timetableLoading = true,
            individualTimeTable = null), {}, modifier,
    )
}

@IFCatalogPreviews
@Composable
internal fun AssessmentsLoadingPreview() = LoadingPage(1) { modifier ->
    DiaryListContent(
        PreviewGrades.copy(sheet = GradesSheet.Subject(1),
            assessments = mapOf(1 to SubjectAssessments())), {}, modifier,
    )
}

@IFCatalogPreviews
@Composable
internal fun ProfileLoadingPreview() = LoadingPage(-1) { modifier -> StudentInfoLoading(modifier) }

@Composable
private fun LoadingPage(selected: Int, content: @Composable (Modifier) -> Unit) {
    IFTheme {
        Scaffold(
            topBar = {
                IFHeader({}, periodControl = {
                    if (selected == 1) GradesPeriodControl(PreviewGrades, {})
                })
            },
            bottomBar = {
                IFBottomNavigation(
                    listOf(
                        IFNavigationItem(stringResource(R.string.today_label), IFIcon.Home),
                        IFNavigationItem(stringResource(R.string.diaries), IFIcon.Grades),
                        IFNavigationItem(stringResource(R.string.calendar), IFIcon.Calendar),
                    ), selected, {},
                )
            },
        ) { padding -> content(Modifier.padding(padding)) }
    }
}
