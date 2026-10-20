package com.adielcalixto.ifacademico.domain

sealed interface NetworkError : Error.DataError {
    data class BadRequest(val message: String) : NetworkError
    data object InvalidCredentials : NetworkError
    data object LoginRequired : NetworkError
    data object Protocol : NetworkError
    data object Storage : NetworkError
    data class Http(val status: Int, val retryAt: Long? = null) : NetworkError
}
