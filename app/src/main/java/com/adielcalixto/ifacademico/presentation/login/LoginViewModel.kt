package com.adielcalixto.ifacademico.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.LoginUseCase
import com.adielcalixto.ifacademico.presentation.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val useCase: LoginUseCase) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.LoginClicked -> {
                viewModelScope.launch {
                    _state.update { it.copy(loading = true) }

                    val result = useCase.execute(_state.value.registration, _state.value.password)
                    when (result) {
                        is Result.Error -> _state.update { it.copy(error = result.error.asUiText()) }
                        is Result.Success -> {
                            action.callback.invoke()
                            _state.update { it.copy(registration = "", password = "") }
                        }
                    }

                    _state.update { it.copy(loading = false) }
                }
            }

            is LoginAction.PasswordChanged -> _state.update { it.copy(password = action.newValue) }
            is LoginAction.UsernameChanged -> _state.update { it.copy(registration = action.newValue) }
            is LoginAction.TogglePasswordClicked -> _state.update { it.copy(showPassword = !it.showPassword) }
            is LoginAction.DismissErrorClicked -> _state.update { it.copy(error = null) }
            is LoginAction.DismissPrivacyPolicy -> _state.update { it.copy(showPrivacyPolicy = false) }
            is LoginAction.DismissTOS -> _state.update { it.copy(showTOS = false) }
            is LoginAction.ShowPrivacyPolicy -> _state.update { it.copy(showPrivacyPolicy = true) }
            is LoginAction.ShowTOS -> _state.update { it.copy(showTOS = true) }
        }
    }
}