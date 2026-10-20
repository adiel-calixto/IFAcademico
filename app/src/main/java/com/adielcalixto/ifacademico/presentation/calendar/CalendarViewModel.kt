package com.adielcalixto.ifacademico.presentation.calendar

import com.adielcalixto.ifacademico.data.session.AcademicFreshness
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.GetSpecialDatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
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
    private val freshness: AcademicFreshness = AcademicFreshness(),
) : ViewModel() {
    private val _state = MutableStateFlow(CalendarState(LocalDate.now().withDayOfMonth(1)))
    val state = _state.asStateFlow()
    private var loadJob: Job? = null
    private var request = 0
    private var retrying = false
    private var revision: Long = freshness.revision.value

    fun refreshIfStale(currentRevision: Long) {
        val previous = revision
        revision = currentRevision
        if (previous != currentRevision && !retrying) loadSpecialDates()
    }

    init {
        loadSpecialDates()
    }

    private fun loadSpecialDates(clear: Boolean = false, retry: Boolean = false) {
        val version = ++request
        loadJob?.cancel()
        val month = YearMonth.from(_state.value.selectedDate)
        _state.update {
            it.copy(
                isLoading = true,
                error = null,
                specialDates = if (clear) emptyList() else it.specialDates
            )
        }
        retrying = retry
        loadJob = viewModelScope.launch {
            try {
                val result = getSpecialDatesUseCase.execute(month.atDay(1), month.atEndOfMonth())
                // A data source may finish after cancellation; publish only the current month request.
                currentCoroutineContext().ensureActive()
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
            } finally {
                if (version == request) {
                    if (retry) revision = freshness.revision.value
                    retrying = false
                    _state.update { it.copy(isLoading = false) }
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
                loadSpecialDates(clear = true)
            }

            CalendarAction.Retry -> if (!_state.value.isLoading) loadSpecialDates(retry = true)
        }
    }
}
