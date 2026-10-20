package com.adielcalixto.ifacademico.presentation.diary_list

import com.adielcalixto.ifacademico.domain.entities.Period

sealed interface DiaryListAction {
    data object LoadViewModelData : DiaryListAction
    data class SelectPeriod(val period: Period) : DiaryListAction
    data object ShowPeriods : DiaryListAction
    data class ShowSubject(val diaryId: Int) : DiaryListAction
    data class RetryAssessments(val diaryId: Int) : DiaryListAction
    data object CloseSheet : DiaryListAction
}
