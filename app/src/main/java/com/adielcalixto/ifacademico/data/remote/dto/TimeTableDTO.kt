package com.adielcalixto.ifacademico.data.remote.dto

import com.adielcalixto.ifacademico.domain.entities.TimeTable
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class TimeTableDTO (
    @SerializedName("aulas") val classes: List<TimeTableClassDTO>,
    @SerializedName("hoje") val today: String,
    @SerializedName("horarioServidor") val serverTime: String
) {
    fun toDomain(): TimeTable {
        return TimeTable(
            classes.map { it.toDomain() },
            today,
            serverTime
        )
    }
}