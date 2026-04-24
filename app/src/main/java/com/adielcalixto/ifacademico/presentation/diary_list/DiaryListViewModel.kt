package com.adielcalixto.ifacademico.presentation.diary_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.GetDiariesUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetPeriodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryListViewModel @Inject constructor(
    private val getPeriodsUseCase: GetPeriodsUseCase,
    private val getDiariesUseCase: GetDiariesUseCase,
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
                    error = null
                )
            }
        }
    }

    fun onAction(action: DiaryListAction) {
        when (action) {
            is DiaryListAction.LoadViewModelData -> {
                loadData()
            }
            is DiaryListAction.SelectPeriod -> {
                _state.update { it.copy(selectedPeriod = action.period) };
                loadData()
            }
        }
    }
}
