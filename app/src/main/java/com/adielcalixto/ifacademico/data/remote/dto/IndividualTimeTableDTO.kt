package com.adielcalixto.ifacademico.data.remote.dto

import com.adielcalixto.ifacademico.domain.entities.IndividualTimeTable
import com.adielcalixto.ifacademico.domain.entities.TimeTableClass
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class IndividualTimeTableDTO(
    @SerializedName("horarios") val courses: List<TimeTableClassDTO>,
    @SerializedName("diaDaSemana") val weekDay: Int
) {
    fun toDomain(): IndividualTimeTable {
        val coursesByDay = mutableMapOf<String, MutableMap<Int, TimeTableClass>>();

        courses.forEach { d ->
            coursesByDay
                .getOrPut(d.startTime) { mutableMapOf() }[d.weekDay!!] = d.toDomain()
        }

        return IndividualTimeTable(
            classes = coursesByDay,
            weekDay = weekDay
        )
    }
}