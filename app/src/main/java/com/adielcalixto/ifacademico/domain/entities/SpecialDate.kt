package com.adielcalixto.ifacademico.domain.entities

import java.time.LocalDateTime

data class SpecialDate(
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val description: String,
    val color: String,
    val type: String,
    val professorName: String?,
    val classDescription: String?
)