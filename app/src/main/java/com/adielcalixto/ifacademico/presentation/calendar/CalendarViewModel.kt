package com.adielcalixto.ifacademico.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.GetSpecialDatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getSpecialDatesUseCase: GetSpecialDatesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CalendarState(LocalDate.now().withDayOfMonth(1)))
    val state = _state.asStateFlow()
    private var loadJob: Job? = null
    private var request = 0

    init {
        loadSpecialDates()
    }

    private fun loadSpecialDates() {
        val version = ++request
        loadJob?.cancel()
        val month = YearMonth.from(_state.value.selectedDate)
        _state.update { it.copy(isLoading = true, error = null, specialDates = emptyList()) }
        loadJob = viewModelScope.launch {
            val result = getSpecialDatesUseCase.execute(month.atDay(1), month.atEndOfMonth())
            // A data source may finish after cancellation; publish only the current month request.
            if (version != request) return@launch
            when (result) {
                is Result.Error -> _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.error
                    )
                }

                is Result.Success -> _state.update {
                    it.copy(
                        isLoading = false,
                        specialDates = specialDatesForMonth(result.data, month)
                    )
                }
            }
        }
    }

    fun onAction(action: CalendarAction) {
        when (action) {
            is CalendarAction.OnMonthChanged -> {
                val date = action.date.withDayOfMonth(1)
                if (date == _state.value.selectedDate) return
                _state.update { it.copy(selectedDate = date) }
                loadSpecialDates()
            }

            CalendarAction.Retry -> if (!_state.value.isLoading) loadSpecialDates()
        }
    }
}
