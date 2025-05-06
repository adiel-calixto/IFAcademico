package com.adielcalixto.ifacademico.domain.entities

data class TimeTableClass (
    val id: Int,
    val className: String,
    val professorName: String,
    val academicPeriod: UByte,
    val startTime: String,
    val endTime: String,
    val classRoomName: String,
    val finished: Boolean
)