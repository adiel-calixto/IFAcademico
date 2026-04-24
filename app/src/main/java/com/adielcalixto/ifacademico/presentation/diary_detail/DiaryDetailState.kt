package com.adielcalixto.ifacademico.presentation.diary_detail

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.entities.Diary
import com.adielcalixto.ifacademico.domain.entities.Exam

data class DiaryDetailState(
    internal val registrationId: Int = 0,
    val diary: Diary? = null,
    val exams: List<Exam> = emptyList(),
    val isLoading: Boolean = false,
    val error: Error.DataError? = null
)

sealed class DiaryDetailAction {
    data object LoadData : DiaryDetailAction()
}
