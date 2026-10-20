package com.adielcalixto.ifacademico.presentation.diary_list

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adielcalixto.ifacademico.domain.entities.Diary
import com.adielcalixto.ifacademico.domain.entities.Exam
import com.adielcalixto.ifacademico.domain.entities.Period
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.*
import java.time.LocalDateTime

internal val PreviewSubject = Diary(
    1, "Algoritmos e pensamento computacional", 80, "2026", "Professora Maria Costa",
    16, 20, 4, 1, 2, 40, 32, 8
)
internal val PreviewAssessments = listOf(
    Exam("Avaliação escrita sobre algoritmos e complexidade", LocalDateTime.of(2026, 10, 19, 8, 0), null, 0f, 1, "A1", 10f, "1"),
    Exam("Trabalho prático", null, null, null, 2, "A2", 10f, "1"),
    Exam("Média da primeira etapa", null, "(A1 + A2) / 2", 8.5f, 3, "Média", 10f, "1"),
    Exam("Projeto final", null, null, null, 4, "P1", 10f, "2")
)
internal val PreviewGrades = DiaryListState(
    registrationId = 1,
    periods = listOf(Period(2026, 2), Period(2026, 1), Period(2025, 2)),
    selectedPeriod = Period(2026, 2),
    diaries = listOf(PreviewSubject, PreviewSubject.copy(id = 2, name = "Banco de dados", hours = 60)),
    assessments = mapOf(1 to SubjectAssessments(PreviewAssessments, false), 2 to SubjectAssessments(emptyList(), false)),
    isLoading = false
)

@IFCatalogPreviews
@Composable
internal fun GradesPreview() {
    IFTheme {
        Scaffold(
            topBar = { IFHeader({}, periodControl = { GradesPeriodControl(PreviewGrades, {}) }) },
            bottomBar = {
                IFBottomNavigation(listOf(
                    IFNavigationItem("Hoje", IFIcon.Home), IFNavigationItem("Diários", IFIcon.Grades),
                    IFNavigationItem("Calendário", IFIcon.Calendar)
                ), 1, {})
            }
        ) { padding -> DiaryListContent(PreviewGrades, {}, Modifier.padding(padding)) }
    }
}

@IFCatalogPreviews
@Composable
internal fun GradesPeriodPreview() {
    IFTheme { DiaryListContent(PreviewGrades.copy(sheet = GradesSheet.PeriodPicker), {}) }
}

@IFCatalogPreviews
@Composable
internal fun GradesDetailPreview() {
    IFTheme { DiaryListContent(PreviewGrades.copy(sheet = GradesSheet.Subject(1)), {}) }
}
