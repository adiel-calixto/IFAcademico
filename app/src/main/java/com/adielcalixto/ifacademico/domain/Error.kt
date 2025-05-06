package com.adielcalixto.ifacademico.domain

sealed interface Error {
    sealed interface DataError: Error
}