package com.adielcalixto.ifacademico.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.GetSpecialDatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val getSpecialDatesUseCase: GetSpecialDatesUseCase): ViewModel() {
    private val _monthOptions = computeMonthOptions()

    private val _state = MutableStateFlow(CalendarState(_monthOptions[1]))
    val state = _state.asStateFlow()

    init {
        loadSpecialDates()
    }

    private fun computeMonthOptions(): List<LocalDate> {
        val currentMonth = LocalDate.now().monthValue
        val currentYear = LocalDate.now().year

        return (currentMonth - 1..currentMonth + 3).map {
            LocalDate.of(if (it == 0) currentYear.dec() else currentYear, if(it == 0) 12 else it, 1)
        }
    }

    fun getMonthOptions(): List<LocalDate> {
        return _monthOptions
    }

    private fun loadSpecialDates() {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val selectedDate = state.value.selectedDate

            val result = getSpecialDatesUseCase.execute(
                selectedDate.withDayOfMonth(1),
                selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())
            )

            when(result) {
                is Result.Error -> { _state.update { it.copy(error=result.error) } }
                is Result.Success -> {
                    val filteredDates =  result.data.filter { it.type != "Aula" }
                    _state.update { it.copy(specialDates = filteredDates, error = null) }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onAction(action: CalendarAction) {
        when(action) {
            is CalendarAction.OnMonthChanged -> {
                _state.update { it.copy(selectedDate = action.date) }
                loadSpecialDates()
            }
        }
    }
}