package com.adielcalixto.ifacademico.presentation

import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.NetworkError
import com.adielcalixto.ifacademico.domain.UnknownError

fun Error.DataError.asUiText(): UiText {
    return when(this) {
        is NetworkError.BadRequest -> UiText.DynamicString(this.message)
        UnknownError -> UiText.StringResource(R.string.unknown_error)
        IOError -> UiText.StringResource(R.string.io_error)
    }
}

fun Theme.asUiText(): UiText {
    return when(this) {
        Theme.LIGHT -> UiText.StringResource(R.string.light_theme)
        Theme.DARK -> UiText.StringResource(R.string.dark_theme)
        Theme.PURPLE -> UiText.StringResource(R.string.purple_theme)
    }
}