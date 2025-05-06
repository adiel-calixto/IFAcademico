package com.adielcalixto.ifacademico.data.remote.dto;

import com.google.gson.annotations.SerializedName

data class LoginErrorDTO(
    @SerializedName("") val messages: List<String> = emptyList()
)