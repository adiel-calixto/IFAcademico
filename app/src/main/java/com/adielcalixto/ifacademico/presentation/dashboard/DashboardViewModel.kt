package com.adielcalixto.ifacademico.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.PerformanceCoefficients
import com.adielcalixto.ifacademico.domain.entities.TimeTable
import com.adielcalixto.ifacademico.domain.usecases.GetIndividualTimeTableUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetPerformanceCoefficientsUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetPeriodsUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetTimeTableUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getPerformanceCoefficientsUseCase: GetPerformanceCoefficientsUseCase,
    private val getTimeTableUseCase: GetTimeTableUseCase,
    private val getPeriodsUseCase: GetPeriodsUseCase,
    private val getIndividualTimeTableUseCase: GetIndividualTimeTableUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(
        DashboardState(
            TimeTable(emptyList(), "", ""),
            PerformanceCoefficients(0f, 0f, 0f)
        )
    )
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val performanceCoefficientsResult = getPerformanceCoefficientsUseCase.execute()) {
                is Result.Error -> _state.update { it.copy(error = performanceCoefficientsResult.error) }
                is Result.Success -> _state.update { it.copy(performanceCoefficients = performanceCoefficientsResult.data) }
            }

            when (val timeTableResult = getTimeTableUseCase.execute()) {
                is Result.Error -> _state.update { it.copy(error = timeTableResult.error) }
                is Result.Success -> _state.update { it.copy(timeTable = timeTableResult.data) }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadPeriodAndIndividualTimeTable() {
        if (_state.value.actualPeriod == null) {
            when (val periodsResult = getPeriodsUseCase.execute()) {
                is Result.Error -> {
                    _state.update { it.copy(error = periodsResult.error) }
                    return
                }
                is Result.Success -> {
                    _state.update {
                        it.copy(actualPeriod = periodsResult.data.first())
                    }
                }
            }
        }

        val actualPeriod = _state.value.actualPeriod ?: return

        when (val result = getIndividualTimeTableUseCase.execute(actualPeriod.year, actualPeriod.number)) {
            is Result.Error -> {
                _state.update { it.copy(error = result.error) }
            }
            is Result.Success -> {
                _state.update {
                    it.copy(individualTimeTable = result.data)
                }
            }
        }
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.LoadViewModelData -> {
                loadData()
            }
            is DashboardAction.ShowIndividualTimeTable -> {
                viewModelScope.launch {
                    loadPeriodAndIndividualTimeTable()
                }
            }
        }
    }
}