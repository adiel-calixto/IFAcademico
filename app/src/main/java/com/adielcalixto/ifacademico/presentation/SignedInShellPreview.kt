package com.adielcalixto.ifacademico.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import com.adielcalixto.ifacademico.domain.entities.Student
import com.adielcalixto.ifacademico.presentation.calendar.CalendarAction
import com.adielcalixto.ifacademico.presentation.calendar.CalendarContent
import com.adielcalixto.ifacademico.presentation.calendar.components.PreviewCalendar
import com.adielcalixto.ifacademico.presentation.calendar.components.PreviewCalendarDate
import com.adielcalixto.ifacademico.presentation.components.IFCatalogPreviews
import com.adielcalixto.ifacademico.presentation.dashboard.DashboardContent
import com.adielcalixto.ifacademico.presentation.dashboard.PreviewDashboard
import com.adielcalixto.ifacademico.presentation.diary_list.DiaryListContent
import com.adielcalixto.ifacademico.presentation.diary_list.GradesPeriodControl
import com.adielcalixto.ifacademico.presentation.diary_list.PreviewGrades
import com.adielcalixto.ifacademico.presentation.student_info.StudentInfoContent

@IFCatalogPreviews
@Composable
internal fun SignedInShellPreview() {
    var calendar by remember { mutableStateOf(PreviewCalendar) }
    IFTheme(darkTheme = isSystemInDarkTheme()) {
        SignedInShell(
            periodControl = { focus ->
                GradesPeriodControl(PreviewGrades, {}, Modifier.focusRequester(focus))
            },
            pageContent = { tab, focus ->
                when (tab) {
                    MainTab.Today -> DashboardContent(PreviewDashboard, {})
                    MainTab.Diaries -> DiaryListContent(PreviewGrades, {}, periodFocus = focus)
                    MainTab.Calendar -> CalendarContent(
                        calendar,
                        { action ->
                            if (action is CalendarAction.OnMonthChanged) {
                                calendar = calendar.copy(selectedDate = action.date)
                            }
                        },
                        today = PreviewCalendarDate,
                    )
                }
            },
            profileContent = {
                StudentInfoContent(
                    Student("Ana Maria de Oliveira", 42, 1, "Bacharelado em Ciência da Computação"),
                    Theme.SYSTEM, {}, true, {}, {}, {},
                )
            },
        )
    }
}
