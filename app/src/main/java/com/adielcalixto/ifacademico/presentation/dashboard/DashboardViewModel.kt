package com.adielcalixto.ifacademico.presentation.dashboard

import com.adielcalixto.ifacademico.data.session.AcademicFreshness
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.GetIndividualTimeTableUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetPerformanceCoefficientsUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetPeriodsUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetTimeTableUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getPerformanceCoefficientsUseCase: GetPerformanceCoefficientsUseCase,
    private val getTimeTableUseCase: GetTimeTableUseCase,
    private val getPeriodsUseCase: GetPeriodsUseCase,
    private val getIndividualTimeTableUseCase: GetIndividualTimeTableUseCase,
    freshness: AcademicFreshness = AcademicFreshness(),
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()
    private var homeJob: Job? = null
    private var timetableJob: Job? = null
    private var timetableRequest = 0
    private var homeRequest = 0
    private var revision: Long = freshness.revision.value
    private var timetableStale = false

    fun refreshIfStale(currentRevision: Long) {
        val previous = revision
        revision = currentRevision
        if (previous == currentRevision) return
        timetableStale = true
        loadData(refresh = true)
        if (_state.value.sheet == DashboardSheet.Timetable) {
            ++timetableRequest
            timetableJob?.cancel()
            timetableJob = null
            loadTimetable()
        }
    }

    init {
        loadData()
    }

    private fun loadData(refresh: Boolean = false) {
        if (homeJob?.isActive == true && !refresh) return
        val request = ++homeRequest
        if (refresh) homeJob?.cancel()
        _state.update { it.copy(isLoading = true, error = null) }
        homeJob = viewModelScope.launch {
            try {
                when (val result = getPerformanceCoefficientsUseCase.execute()) {
                    is Result.Error -> {
                        currentCoroutineContext().ensureActive()
                        if (request != homeRequest) return@launch
                        _state.update { it.copy(error = result.error) }
                    }

                    is Result.Success -> {
                        currentCoroutineContext().ensureActive()
                        if (request != homeRequest) return@launch
                        _state.update { it.copy(performanceCoefficients = result.data) }
                    }
                }
                when (val result = getTimeTableUseCase.execute()) {
                    is Result.Error -> {
                        currentCoroutineContext().ensureActive()
                        if (request != homeRequest) return@launch
                        _state.update { it.copy(error = result.error) }
                    }

                    is Result.Success -> {
                        currentCoroutineContext().ensureActive()
                        if (request != homeRequest) return@launch
                        _state.update {
                            it.copy(
                                timeTable = result.data.copy(
                                    classes = sortedClasses(
                                        result.data.classes
                                    )
                                ), hasContent = true
                            )
                        }
                        val detail = _state.value.sheet as? DashboardSheet.ClassDetail
                        if (detail != null) {
                            val course =
                                result.data.classes.firstOrNull { it.id == detail.course.id }
                            _state.update {
                                it.copy(
                                    sheet = course?.let(DashboardSheet::ClassDetail)
                                        ?: DashboardSheet.Closed
                                )
                            }
                        }
                    }
                }
            } finally {
                if (request == homeRequest) _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadTimetable() {
        if (timetableJob?.isActive == true) return
        val request = ++timetableRequest
        _state.update { it.copy(timetableLoading = true, timetableError = null) }
        timetableJob = viewModelScope.launch {
            try {
                val periods = getPeriodsUseCase.execute()
                currentCoroutineContext().ensureActive()
                if (request != timetableRequest) return@launch
                when (periods) {
                    is Result.Error -> _state.update {
                        it.copy(timetableLoading = false, timetableError = periods.error)
                    }

                    is Result.Success -> {
                        val period = periods.data.firstOrNull()
                        _state.update { it.copy(currentPeriod = period) }
                        if (period == null) {
                            _state.update { it.copy(timetableLoading = false) }
                            return@launch
                        }
                        val result =
                            getIndividualTimeTableUseCase.execute(period.year, period.number)
                        currentCoroutineContext().ensureActive()
                        if (request != timetableRequest) return@launch
                        when (result) {
                            is Result.Error -> _state.update {
                                it.copy(timetableLoading = false, timetableError = result.error)
                            }

                            is Result.Success -> {
                                timetableStale = false
                                _state.update {
                                    it.copy(
                                        individualTimeTable = result.data,
                                        selectedWeekday = if (it.individualTimeTable != null) it.selectedWeekday
                                        else result.data.weekDay.takeIf { day -> day in 1..7 } ?: 2,
                                        timetableLoading = false
                                    )
                                }
                            }
                        }
                    }
                }
            } finally {
                if (request == timetableRequest) _state.update { it.copy(timetableLoading = false) }
            }
        }
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            DashboardAction.LoadViewModelData -> loadData()
            DashboardAction.ShowIndividualTimeTable -> {
                _state.update { it.copy(sheet = DashboardSheet.Timetable) }
                if (_state.value.individualTimeTable == null || timetableStale) loadTimetable()
            }

            DashboardAction.RetryTimetable -> loadTimetable()
            DashboardAction.HideIndividualTimeTable -> {
                ++timetableRequest
                timetableJob?.cancel()
                timetableJob = null
                _state.update { it.copy(sheet = DashboardSheet.Closed, timetableLoading = false) }
            }

            is DashboardAction.SelectWeekday -> if (action.weekday in 1..7) {
                _state.update { it.copy(selectedWeekday = action.weekday) }
            }

            is DashboardAction.ShowClass -> _state.update {
                it.copy(sheet = DashboardSheet.ClassDetail(action.course))
            }
        }
    }
}
