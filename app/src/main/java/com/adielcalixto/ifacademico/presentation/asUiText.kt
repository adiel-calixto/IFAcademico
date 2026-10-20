package com.adielcalixto.ifacademico.presentation

import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.NetworkError
import com.adielcalixto.ifacademico.domain.UnknownError

fun Error.DataError.asUiText(): UiText {
    return when (this) {
        is NetworkError.BadRequest -> UiText.DynamicString(this.message)
        NetworkError.InvalidCredentials -> UiText.StringResource(R.string.invalid_credentials)
        NetworkError.LoginRequired -> UiText.StringResource(R.string.session_login_required)
        NetworkError.Protocol -> UiText.StringResource(R.string.session_protocol_error)
        NetworkError.Storage -> UiText.StringResource(R.string.session_storage_error)
        is NetworkError.Http -> UiText.StringResource(if (status == 429) R.string.session_throttled else R.string.session_server_error)
        UnknownError -> UiText.StringResource(R.string.unknown_error)
        IOError -> UiText.StringResource(R.string.io_error)
    }
}

fun Theme.asUiText(): UiText {
    return when (this) {
        Theme.LIGHT -> UiText.StringResource(R.string.light_theme)
        Theme.DARK -> UiText.StringResource(R.string.dark_theme)
        Theme.SYSTEM -> UiText.StringResource(R.string.system_theme)
    }
}