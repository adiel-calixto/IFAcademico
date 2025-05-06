package com.adielcalixto.ifacademico.data.remote.dto

import com.adielcalixto.ifacademico.domain.entities.Diary
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class DiaryDTO(
    val id: Int,
    @SerializedName("disciplina") val name: String,
    @SerializedName("cargaHoraria") val hours: Int,
    @SerializedName("anoLetivo") val year: String,
    @SerializedName("professores") val professor: String,
    @SerializedName("faltas") val absences: Int,
    @SerializedName("maxNFaltas") val maxAbsences: Int,
    @SerializedName("faltasRestante") val remainingAbsences: Int,
    @SerializedName("faltasAbonadas") val excusedAbsences: Int,
    @SerializedName("periodoLetivo") val academicPeriod: Int,
    @SerializedName("aulasPendentes") val pendingClasses: Int,
) {
    fun toDomain(): Diary {
        return Diary(
            id = id,
            name = name,
            hours = hours,
            year = year,
            professor = professor,
            absences = absences,
            maxAbsences = maxAbsences,
            remainingAbsences = remainingAbsences,
            excusedAbsences = excusedAbsences,
            academicPeriod = academicPeriod
        )
    }
}