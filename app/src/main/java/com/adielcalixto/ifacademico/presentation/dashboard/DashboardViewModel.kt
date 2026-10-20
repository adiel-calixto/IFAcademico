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
    private val freshness: AcademicFreshness = AcademicFreshness(),
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()
    private var coefficientsJob: Job? = null
    private var classesJob: Job? = null
    private var coefficientsRetrying = false
    private var classesRetrying = false
    private var timetableRetrying = false
    private var timetableJob: Job? = null
    private var timetableRequest = 0
    private var coefficientsRequest = 0
    private var classesRequest = 0
    private var revision: Long = freshness.revision.value
    private var timetableStale = false

    fun refreshIfStale(currentRevision: Long) {
        val previous = revision
        revision = currentRevision
        if (previous == currentRevision) return
        timetableStale = true
        if ((coefficientsRetrying && coefficientsJob?.isActive == true) ||
            (classesRetrying && classesJob?.isActive == true) ||
            (timetableRetrying && timetableJob?.isActive == true)
        ) return
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
        loadCoefficients(refresh)
        loadClasses(refresh)
    }

    private fun loadCoefficients(refresh: Boolean = false, retry: Boolean = false) {
        if (coefficientsJob?.isActive == true && !refresh) return
        val request = ++coefficientsRequest
        coefficientsJob?.cancel()
        _state.update { it.copy(coefficientsLoading = true, coefficientsError = null) }
        coefficientsRetrying = retry
        coefficientsJob = viewModelScope.launch {
            try {
                val result = getPerformanceCoefficientsUseCase.execute()
                currentCoroutineContext().ensureActive()
                if (request != coefficientsRequest) return@launch
                when (result) {
                    is Result.Error -> _state.update { it.copy(coefficientsError = result.error) }
                    is Result.Success -> _state.update { it.copy(performanceCoefficients = result.data) }
                }
            } finally {
                if (retry && request == coefficientsRequest) {
                    if (revision != freshness.revision.value) timetableStale = true
                    revision = freshness.revision.value
                }
                if (request == coefficientsRequest) _state.update { it.copy(coefficientsLoading = false) }
            }
        }
    }

    private fun loadClasses(refresh: Boolean = false, retry: Boolean = false) {
        if (classesJob?.isActive == true && !refresh) return
        val request = ++classesRequest
        classesJob?.cancel()
        _state.update { it.copy(classesLoading = true, classesError = null) }
        classesRetrying = retry
        classesJob = viewModelScope.launch {
            try {
                val result = getTimeTableUseCase.execute()
                currentCoroutineContext().ensureActive()
                if (request != classesRequest) return@launch
                when (result) {
                    is Result.Error -> _state.update { it.copy(classesError = result.error) }
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                timeTable = result.data.copy(
                                    classes = sortedClasses(
                                        result.data.classes
                                    )
                                )
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
                if (retry && request == classesRequest) {
                    if (revision != freshness.revision.value) timetableStale = true
                    revision = freshness.revision.value
                }
                if (request == classesRequest) _state.update { it.copy(classesLoading = false) }
            }
        }
    }

    private fun loadTimetable(retry: Boolean = false) {
        if (timetableJob?.isActive == true) return
        val request = ++timetableRequest
        _state.update { it.copy(timetableLoading = true, timetableError = null) }
        timetableRetrying = retry
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
                if (retry && request == timetableRequest) revision = freshness.revision.value
                if (request == timetableRequest) _state.update { it.copy(timetableLoading = false) }
            }
        }
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            DashboardAction.RetryHome -> {
                loadCoefficients(retry = true)
                loadClasses(retry = true)
            }

            DashboardAction.RetryCoefficients -> loadCoefficients(retry = true)
            DashboardAction.RetryClasses -> loadClasses(retry = true)
            DashboardAction.ShowIndividualTimeTable -> {
                _state.update { it.copy(sheet = DashboardSheet.Timetable) }
                if (_state.value.individualTimeTable == null || timetableStale) loadTimetable()
            }

            DashboardAction.RetryTimetable -> loadTimetable(retry = true)
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
