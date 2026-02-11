package com.adielcalixto.ifacademico.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.PerformanceCoefficients
import com.adielcalixto.ifacademico.domain.entities.TimeTable
import com.adielcalixto.ifacademico.domain.usecases.GetPerformanceCoefficientsUseCase
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
        private val getTimeTableUseCase: GetTimeTableUseCase
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

    fun onAction(action: DashboardAction) {
        when (action) {
            DashboardAction.LoadViewModelData -> {
                loadData()
            }
        }
    }
}