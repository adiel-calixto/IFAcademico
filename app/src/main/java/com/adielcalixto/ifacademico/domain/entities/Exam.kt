package com.adielcalixto.ifacademico.domain.entities

import java.time.LocalDateTime

data class Exam(
    val description: String,
    val date: LocalDateTime?,
    val formula: String?,
    val grade: Float,
    val order: Int,
    val acronym: String,
    val maxGrade: Float,
    val stepId: String
)