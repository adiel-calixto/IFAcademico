package com.adielcalixto.ifacademico.presentation.diary_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.GetDiariesUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetExamsUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetIndividualTimeTableUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetPeriodsUseCase
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializer
import javax.inject.Inject

@HiltViewModel
class DiaryListViewModel @Inject constructor(
    private val getPeriodsUseCase: GetPeriodsUseCase,
    private val getDiariesUseCase: GetDiariesUseCase,
    private val getExamsUseCase: GetExamsUseCase,
    private val getIndividualTimeTableUseCase: GetIndividualTimeTableUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(DiaryListState(0))
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun setRegistrationId(registrationId: Int) {
        _state.update { it.copy(registrationId = registrationId) }
    }

    private fun loadData() {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            if (_state.value.periods.isEmpty()) {
                when (val periodsResult = getPeriodsUseCase.execute()) {
                    is Result.Error -> {
                        _state.update { it.copy(error = periodsResult.error) }
                    }

                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                periods = periodsResult.data,
                                selectedPeriod = periodsResult.data.first(),
                                error = null
                            )
                        }
                    }
                }
            }

            loadDiaries()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadDiaries() {
        val selectedPeriod = state.value.selectedPeriod ?: return

        val result =
            getDiariesUseCase.execute(_state.value.registrationId, selectedPeriod.number, selectedPeriod.year)

        when (result) {
            is Result.Error -> _state.update { it.copy(error = result.error) }
            is Result.Success -> _state.update {
                it.copy(
                    diaries = result.data,
                    examsMap = hashMapOf(),
                    error = null
                )
            }
        }
    }

    private fun loadExams(diaryId: Int) {
        if (state.value.examsMap.containsKey(diaryId))
            return

        viewModelScope.launch {
            when (val result = getExamsUseCase.execute(diaryId, _state.value.registrationId)) {
                is Result.Error -> {}
                is Result.Success -> {
                    val newMap = hashMapOf(diaryId to result.data)
                    newMap.putAll(state.first().examsMap)

                    _state.update { it.copy(examsMap = newMap) }
                }
            }
        }
    }

    private fun loadIndividualTimeTable() {
        val selectedPeriod = state.value.selectedPeriod ?: return

        viewModelScope.launch {
            when (val result = getIndividualTimeTableUseCase.execute(selectedPeriod.year, selectedPeriod.number)) {
                is Result.Error -> {}
                is Result.Success -> {
                    _state.update {
                        it.copy(individualTimeTable =  result.data)
                    }
                }
            }
        }
    }

    fun onAction(action: DiaryListAction) {
        when (action) {
            is DiaryListAction.ExpandDiary -> loadExams(action.diaryId)
            is DiaryListAction.LoadViewModelData -> {
                loadData()
            }
            is DiaryListAction.SelectPeriod -> {
                _state.update { it.copy(selectedPeriod = action.period) };
                loadData()
            }
            is DiaryListAction.OpenIndividualTimeTable -> loadIndividualTimeTable()
        }
    }
}