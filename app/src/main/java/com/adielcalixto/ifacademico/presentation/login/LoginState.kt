package com.adielcalixto.ifacademico.presentation.login

import com.adielcalixto.ifacademico.presentation.UiText

data class LoginState(
    val registration: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val showPassword: Boolean = false,
    val error: UiText? = null,
    val showTOS: Boolean = false,
    val showPrivacyPolicy: Boolean = false
) {
}