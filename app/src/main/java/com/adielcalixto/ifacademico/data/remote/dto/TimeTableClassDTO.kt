package com.adielcalixto.ifacademico.data.remote.dto

import com.adielcalixto.ifacademico.domain.entities.TimeTableClass
import com.google.gson.annotations.SerializedName

data class TimeTableClassDTO(
    @SerializedName("idAulaMinistrada") val id: Int,
    @SerializedName("descDisciplina") val className: String,
    @SerializedName("periodo") val academicPeriod: UByte,
    @SerializedName("professor") val professorName: String,
    @SerializedName("horarioInicio") val startTime: String,
    @SerializedName("horarioFim") val endTime: String,
    @SerializedName("descSala") val classRoomName: String,
    @SerializedName("encerrada") val finished: Boolean,
) {
    fun toDomain(): TimeTableClass {
        return TimeTableClass(
            id,
            className,
            professorName,
            academicPeriod,
            startTime,
            endTime,
            classRoomName,
            finished
        )
    }
}
