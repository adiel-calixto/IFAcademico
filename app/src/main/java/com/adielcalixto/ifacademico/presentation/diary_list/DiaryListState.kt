package com.adielcalixto.ifacademico.presentation.diary_list

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.entities.Diary
import com.adielcalixto.ifacademico.domain.entities.Exam
import com.adielcalixto.ifacademico.domain.entities.Period

data class DiaryListState(
    internal val registrationId: Int,
    val periods: List<Period> = emptyList(),
    val selectedPeriod: Period? = null,
    val diaries: List<Diary> = emptyList(),
    val examsMap: HashMap<Int, List<Exam>> = hashMapOf(),
    val isLoading: Boolean = false,
    val error: Error.DataError? = null
)