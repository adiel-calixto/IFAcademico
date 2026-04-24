package com.adielcalixto.ifacademico.presentation.diary_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.GetDiariesUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetExamsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val getDiariesUseCase: GetDiariesUseCase,
    private val getExamsUseCase: GetExamsUseCase,
) : ViewModel() {
    private var diaryId: Int = 0
    private var periodNumber: Int = 0
    private var periodYear: Int = 0

    private val _state = MutableStateFlow(DiaryDetailState())
    val state = _state.asStateFlow()

    fun setRegistrationId(registrationId: Int) {
        _state.update { it.copy(registrationId = registrationId) }
    }

    fun setDiaryAndPeriod(diaryId: Int, periodNumber: Int, periodYear: Int) {
        this.diaryId = diaryId
        this.periodNumber = periodNumber
        this.periodYear = periodYear
        loadData()
    }

    private fun loadData() {
        if (diaryId == 0 || periodNumber == 0 || _state.value.registrationId == 0) return

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = getDiariesUseCase.execute(
                _state.value.registrationId,
                periodNumber,
                periodYear
            )

            when (result) {
                is Result.Error -> _state.update { it.copy(error = result.error, isLoading = false) }
                is Result.Success -> {
                    val diary = result.data.firstOrNull { it.id == diaryId }
                    _state.update { it.copy(diary = diary, isLoading = false) }

                    if (diary != null) {
                        loadExams()
                    }
                }
            }
        }
    }

    private fun loadExams() {
        viewModelScope.launch {
            when (val result = getExamsUseCase.execute(diaryId, _state.value.registrationId)) {
                is Result.Error -> _state.update { it.copy(isLoading = false) }
                is Result.Success -> {
                    _state.update { it.copy(exams = result.data, isLoading = false) }
                }
            }
        }
    }

    fun onAction(action: DiaryDetailAction) {
        when (action) {
            is DiaryDetailAction.LoadData -> loadData()
        }
    }
}
