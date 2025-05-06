package com.adielcalixto.ifacademico.presentation.login

sealed interface LoginAction {
    data class UsernameChanged(val newValue: String): LoginAction
    data class PasswordChanged(val newValue: String): LoginAction
    data class LoginClicked(val callback: () -> Unit): LoginAction
    data object DismissErrorClicked: LoginAction
    data object TogglePasswordClicked: LoginAction
    data object DismissTOS: LoginAction
    data object DismissPrivacyPolicy: LoginAction
    data object ShowTOS: LoginAction
    data object ShowPrivacyPolicy: LoginAction
}