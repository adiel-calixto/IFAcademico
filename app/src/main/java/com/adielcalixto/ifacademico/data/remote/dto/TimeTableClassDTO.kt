package com.adielcalixto.ifacademico.data.remote.dto

import com.adielcalixto.ifacademico.domain.entities.TimeTableClass
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class TimeTableClassDTO(
    @SerializedName("idAulaMinistrada", alternate = ["idDisciplina"]) val id: Int,
    @SerializedName("descDisciplina") val className: String,
    @SerializedName("periodo", alternate = ["anoLet"]) val academicPeriod: UByte,
    @SerializedName("professor", alternate = ["nomeProfessor"]) val professorName: String?,
    @SerializedName("horarioInicio", alternate = ["horaInicio"]) val startTime: String,
    @SerializedName("horarioFim", alternate = ["horaFinal"]) val endTime: String,
    @SerializedName("descSala") val classRoomName: String,
    @SerializedName("encerrada") val finished: Boolean,
    @SerializedName("diaSem") val weekDay: Int?
) {
    fun toDomain(): TimeTableClass {
        return TimeTableClass(
            id,
            className,
            professorName ?: "",
            academicPeriod,
            startTime,
            endTime,
            classRoomName,
            finished,
            weekDay
        )
    }
}
