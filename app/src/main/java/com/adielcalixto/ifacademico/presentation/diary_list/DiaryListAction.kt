package com.adielcalixto.ifacademico.presentation.diary_list

import com.adielcalixto.ifacademico.domain.entities.Period

sealed class DiaryListAction {
    data object LoadViewModelData : DiaryListAction()
    data class ExpandDiary(val diaryId: Int) : DiaryListAction()
    data class SelectPeriod(val period: Period) : DiaryListAction()
}
