package com.adielcalixto.ifacademico.domain.entities

data class TimeTable(
    val classes: List<TimeTableClass>,
    val today: String,
    val serverTime: String
)
