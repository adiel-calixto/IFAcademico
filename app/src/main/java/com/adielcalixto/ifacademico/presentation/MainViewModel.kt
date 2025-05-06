package com.adielcalixto.ifacademico.presentation

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.GetStudentUseCase
import com.adielcalixto.ifacademico.domain.usecases.LogoutUseCase
import com.adielcalixto.ifacademico.domain.usecases.RefreshSessionUseCase
import com.adielcalixto.ifacademico.domain.usecases.SessionState
import com.adielcalixto.ifacademico.domain.usecases.VerifySessionUseCase
import com.adielcalixto.ifacademico.observers.UnauthorizedApiErrorObserver
import com.adielcalixto.ifacademico.observers.UnauthorizedApiErrorSubscriber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val verifySessionUseCase: VerifySessionUseCase,
    private val getStudentUseCase: GetStudentUseCase,
    private val refreshSessionUseCase: RefreshSessionUseCase,
    unauthorizedApiErrorObserver: UnauthorizedApiErrorObserver
) : ViewModel(), UnauthorizedApiErrorSubscriber, DefaultLifecycleObserver {

    private val _state = MutableStateFlow(MainState(isLoading = true, isFirstLoad = true))
    val state = _state.asStateFlow()

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        unauthorizedApiErrorObserver.subscribe(this)

        runSuspendWithLoading(::loadSession)
        _state.update { it.copy(isFirstLoad = false) }
    }

    fun retryLoadingData() {
        runSuspendWithLoading(::loadSession)
    }

    fun onLogin() {
        runSuspendWithLoading { login() }
    }

    fun onLogout() {
        logout()
    }

    override fun onResume(owner: LifecycleOwner) {
        if (_state.value.isFirstLoad) return
        runSuspendWithLoading { loadSession() }
    }

    override fun onCleared() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }

    override fun onUnauthorizedError() {
        logout()
    }

    private suspend fun loadSession() {
        val sessionState = verifySessionUseCase.execute()
        _state.update { it.copy(sessionState = sessionState) }

        when (sessionState) {
            SessionState.Invalid -> {}
            SessionState.Valid -> {
                if (_state.first().student == null) loadStudent()
            }

            SessionState.CanRefresh -> refreshSession()
        }
    }

    private suspend fun refreshSession() {
        when (val result = refreshSessionUseCase.execute()) {
            is Result.Error -> {
                if (result.error is IOError) {
                    _state.update { it.copy(error = result.error) }
                } else {
                    logout()
                }
            }

            is Result.Success -> {
                login()
            }
        }
    }

    private fun logout() {
        logoutUseCase.execute()
        _state.update { it.copy(sessionState = SessionState.Invalid) }
    }

    private suspend fun login() {
        loadStudent()
        _state.update { it.copy(sessionState = SessionState.Valid) }
    }

    private suspend fun loadStudent() {
        when (val result = getStudentUseCase.execute()) {
            is Result.Error -> _state.update { it.copy(error = result.error) }
            is Result.Success -> {
                _state.update { it.copy(student = result.data, error = null) }
            }
        }
    }

    private fun runSuspendWithLoading(action: suspend () -> Unit) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                action()
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}