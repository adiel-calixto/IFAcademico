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
        val coursesByDay = mutableMapOf<Int, MutableList<TimeTableClass>>();

        courses.forEach { d ->
            coursesByDay.getOrPut(d.weekDay!!) { mutableListOf() }.apply {
                add(d.toDomain())
            }
        }

        return IndividualTimeTable(
            classes = coursesByDay,
            weekDay = weekDay
        )
    }
}