package com.adielcalixto.ifacademico.data.remote.dto

data class LoginDTO(
    val login: String,
    val password: String,
    val tipoUsuario: Int = 1
)
