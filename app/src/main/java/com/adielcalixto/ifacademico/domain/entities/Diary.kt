package com.adielcalixto.ifacademico.domain.entities

data class Diary(
    val id: Int,
    val name: String,
    val hours: Int,
    val year: String,
    val professor: String,
    val absences: Int,
    val maxAbsences: Int,
    val remainingAbsences: Int,
    val excusedAbsences: Int,
    val academicPeriod: Int,
)