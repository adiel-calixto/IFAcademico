package com.adielcalixto.ifacademico.presentation.diary_list

import com.adielcalixto.ifacademico.data.session.AcademicFreshness
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.GetDiariesUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetExamsUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetPeriodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject

@HiltViewModel
class DiaryListViewModel @Inject constructor(
    private val getPeriodsUseCase: GetPeriodsUseCase,
    private val getDiariesUseCase: GetDiariesUseCase,
    private val getExamsUseCase: GetExamsUseCase,
    private val freshness: AcademicFreshness = AcademicFreshness(),
) : ViewModel() {
    private val _state = MutableStateFlow(DiaryListState())
    val state = _state.asStateFlow()
    private var loadJob: Job? = null
    private val retryJobs = mutableMapOf<Int, Job>()
    private val assessmentPermits = Semaphore(4)
    private var request = 0
    private var listRetrying = false
    private var periodsStale = false
    private var revision: Long = freshness.revision.value

    fun refreshIfStale(currentRevision: Long) {
        val previous = revision
        revision = currentRevision
        if (previous != currentRevision && !(listRetrying && loadJob?.isActive == true) &&
            retryJobs.values.none { it.isActive }
        ) loadData(refresh = true)
    }

    fun setRegistrationId(registrationId: Int) {
        if (registrationId <= 0 || registrationId == _state.value.registrationId) return
        _state.value = DiaryListState(registrationId = registrationId)
        loadData()
    }

    private fun loadData(refresh: Boolean = false, clear: Boolean = false, retry: Boolean = false) {
        val registrationId = _state.value.registrationId
        if (registrationId <= 0) return
        if (retry && _state.value.isLoading && loadJob?.isActive == true) return
        if (refresh) periodsStale = true
        val version = ++request
        loadJob?.cancel()
        retryJobs.values.forEach { it.cancel() }
        retryJobs.clear()
        _state.update {
            it.copy(
                isLoading = true,
                error = null,
                diaries = if (clear) emptyList() else it.diaries,
                assessments = if (clear) emptyMap() else it.assessments
            )
        }
        listRetrying = retry
        loadJob = viewModelScope.launch {
            try {
                if (periodsStale || _state.value.periods.isEmpty()) {
                    val result = getPeriodsUseCase.execute()
                    currentCoroutineContext().ensureActive()
                    if (version != request) return@launch
                    if (result is Result.Success) periodsStale = false
                    when (result) {
                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false, error = result.error) }
                            return@launch
                        }

                        is Result.Success -> _state.update {
                            it.copy(
                                periods = result.data,
                                selectedPeriod = it.selectedPeriod?.takeIf { period -> period in result.data }
                                    ?: result.data.firstOrNull())
                        }
                    }
                }
                val period = _state.value.selectedPeriod
                if (period == null) {
                    _state.update { it.copy(isLoading = false) }
                    return@launch
                }
                val result = getDiariesUseCase.execute(registrationId, period.number, period.year)
                currentCoroutineContext().ensureActive()
                if (version != request) return@launch
                when (result) {
                    is Result.Error -> _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.error
                        )
                    }

                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false, diaries = result.data,
                                assessments = result.data.associate { diary ->
                                    diary.id to (it.assessments[diary.id]?.copy(
                                        isLoading = true,
                                        error = null
                                    ) ?: SubjectAssessments())
                                }
                            )
                        }
                        coroutineScope {
                            result.data.forEach { diary ->
                                launch { loadAssessments(diary.id, registrationId, version) }
                            }
                        }
                    }
                }
            } finally {
                if (retry && version == request) revision = freshness.revision.value
                if (version == request) _state.update { current ->
                    current.copy(
                        isLoading = false,
                        // Individual retries can outlive the initial assessment batch.
                        assessments = current.assessments.mapValues { (id, assessment) ->
                            if (retryJobs[id]?.isActive == true) assessment else assessment.copy(
                                isLoading = false
                            )
                        })
                }
            }
        }
    }

    private suspend fun loadAssessments(diaryId: Int, registrationId: Int, version: Int) {
        assessmentPermits.withPermit {
            currentCoroutineContext().ensureActive()
            if (version != request) return@withPermit
            val result = getExamsUseCase.execute(diaryId, registrationId)
            // Some data sources can finish after cancellation; never publish an obsolete period.
            currentCoroutineContext().ensureActive()
            if (version != request) return@withPermit
            val assessments = when (result) {
                is Result.Error -> (_state.value.assessments[diaryId] ?: SubjectAssessments()).copy(
                    isLoading = false,
                    error = result.error
                )

                is Result.Success -> SubjectAssessments(exams = result.data, isLoading = false)
            }
            _state.update { it.copy(assessments = it.assessments + (diaryId to assessments)) }
        }
    }

    fun onAction(action: DiaryListAction) {
        when (action) {
            DiaryListAction.LoadViewModelData -> loadData(retry = true)
            is DiaryListAction.SelectPeriod -> {
                if (action.period !in _state.value.periods) return
                val changed = action.period != _state.value.selectedPeriod
                _state.update {
                    it.copy(
                        selectedPeriod = action.period,
                        sheet = GradesSheet.Closed
                    )
                }
                if (changed) loadData(clear = true)
            }

            DiaryListAction.ShowPeriods -> _state.update { it.copy(sheet = GradesSheet.PeriodPicker) }
            is DiaryListAction.ShowSubject -> {
                if (_state.value.diaries.none { it.id == action.diaryId }) return
                _state.update { it.copy(sheet = GradesSheet.Subject(action.diaryId)) }
            }

            DiaryListAction.CloseSheet -> _state.update { it.copy(sheet = GradesSheet.Closed) }
            is DiaryListAction.RetryAssessments -> {
                val current = _state.value
                val assessment = current.assessments[action.diaryId] ?: return
                if (assessment.isLoading || retryJobs[action.diaryId]?.isActive == true) return
                _state.update {
                    it.copy(
                        assessments = it.assessments + (action.diaryId to assessment.copy(
                            isLoading = true,
                            error = null
                        ))
                    )
                }
                val version = request
                retryJobs[action.diaryId] = viewModelScope.launch {
                    try {
                        loadAssessments(action.diaryId, current.registrationId, version)
                    } finally {
                        if (version == request) revision = freshness.revision.value
                        if (version == request) _state.update {
                            val latest = it.assessments[action.diaryId] ?: return@update it
                            it.copy(
                                assessments = it.assessments + (action.diaryId to latest.copy(
                                    isLoading = false
                                ))
                            )
                        }
                    }
                }
            }
        }
    }
}
