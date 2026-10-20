package com.adielcalixto.ifacademico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.data.session.SessionCoordinator
import com.adielcalixto.ifacademico.data.session.SessionPhase
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.GetStudentUseCase
import com.adielcalixto.ifacademico.domain.usecases.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val session: SessionCoordinator,
    private val getStudentUseCase: GetStudentUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()
    val freshness = session.freshness.revision
    private var profileJob: Job? = null
    private var recoveryJob: Job? = null
    private var profileOwner = -1L
    private var loadedRevision = -1L

    init {
        viewModelScope.launch {
            session.state.collect { snapshot ->
                if (snapshot.generation != profileOwner) {
                    profileJob?.cancel()
                    profileOwner = snapshot.generation
                    loadedRevision = -1
                    _state.value = MainState(isFirstLoad = false)
                }
                when (snapshot.phase) {
                    SessionPhase.LoginRequired -> _state.update {
                        it.copy(
                            sessionState = SessionState.Invalid,
                            student = null,
                            error = snapshot.error
                        )
                    }

                    SessionPhase.RecoverableFailure -> _state.update {
                        it.copy(
                            error = snapshot.error,
                            sessionError = snapshot.error,
                            isFirstLoad = false
                        )
                    }

                    SessionPhase.Authenticated -> {
                        _state.update {
                            it.copy(
                                error = null,
                                sessionError = null,
                                isFirstLoad = false,
                                isResolvingIdentity = it.student == null
                            )
                        }
                        if (_state.value.student == null) loadStudent()
                    }

                    else -> Unit
                }
            }
        }
        viewModelScope.launch { session.restore() }
    }

    fun retryLoadingData() {
        loadStudent(force = true)
    }

    fun retryStartup() {
        if (recoveryJob?.isActive == true) return
        _state.update { it.copy(error = null, profileError = null, profileLoading = true) }
        recoveryJob = viewModelScope.launch {
            try {
                if (session.retry() is Result.Success && session.state.value.hasSession) loadStudent(
                    force = true
                )
            } finally {
                if (profileJob?.isActive != true) _state.update { it.copy(profileLoading = false) }
            }
        }
    }

    fun onLogout() {
        session.logout()
        recoveryJob?.cancel()
        profileJob?.cancel()
        _state.value = MainState(isFirstLoad = false, sessionState = SessionState.Invalid)
    }

    fun refreshProfile(revision: Long) {
        if (revision != loadedRevision) loadStudent(force = true)
    }

    private fun loadStudent(force: Boolean = false) {
        if (profileJob?.isActive == true || (!force && _state.value.student != null)) return
        val owner = session.currentGeneration()
        val revision = freshness.value
        _state.update { it.copy(error = null, profileError = null, profileLoading = true) }
        profileJob = viewModelScope.launch {
            try {
                session.owned {
                    val result = getStudentUseCase.execute()
                    currentCoroutineContext().ensureActive()
                    session.publish(owner) {
                        when (result) {
                            is Result.Error -> _state.update {
                                it.copy(
                                    error = if (it.student == null) result.error else null,
                                    profileError = result.error,
                                    isFirstLoad = false
                                )
                            }

                            is Result.Success -> {
                                loadedRevision = revision
                                _state.update {
                                    it.copy(
                                        student = result.data,
                                        error = null,
                                        isResolvingIdentity = false,
                                        sessionState = SessionState.Valid,
                                        isFirstLoad = false
                                    )
                                }
                            }
                        }
                    }
                }
            } finally {
                if (session.isCurrent(owner)) _state.update { it.copy(profileLoading = false) }
            }
        }
    }
}
