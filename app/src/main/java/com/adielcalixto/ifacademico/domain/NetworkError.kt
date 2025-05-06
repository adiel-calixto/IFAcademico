package com.adielcalixto.ifacademico.domain

sealed interface NetworkError: Error.DataError {
    data class BadRequest(val message: String): NetworkError
}