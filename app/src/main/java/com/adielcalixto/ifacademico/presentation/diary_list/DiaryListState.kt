package com.adielcalixto.ifacademico.presentation.diary_list

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.entities.Diary
import com.adielcalixto.ifacademico.domain.entities.Exam
import com.adielcalixto.ifacademico.domain.entities.Period

data class DiaryListState(
    internal val registrationId: Int = 0,
    val periods: List<Period> = emptyList(),
    val selectedPeriod: Period? = null,
    val diaries: List<Diary> = emptyList(),
    val assessments: Map<Int, SubjectAssessments> = emptyMap(),
    val isLoading: Boolean = true,
    val hasContent: Boolean = false,
    val error: Error.DataError? = null,
    val sheet: GradesSheet = GradesSheet.Closed,
)

data class SubjectAssessments(
    val exams: List<Exam> = emptyList(),
    val isLoading: Boolean = true,
    val error: Error.DataError? = null,
)

sealed interface GradesSheet {
    data object Closed : GradesSheet
    data object PeriodPicker : GradesSheet
    data class Subject(val diaryId: Int) : GradesSheet
}
