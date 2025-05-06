package com.adielcalixto.ifacademico.data.remote.dto

import com.adielcalixto.ifacademico.domain.entities.SpecialDate
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class SpecialDateDTO (
    @SerializedName("dataEspecialCalendario") val startDate: String,
    @SerializedName("dataFinal") val endDate: String,
    @SerializedName("descricaoDataEspecialCalendario")val description: String,
    @SerializedName("corHexadecimal")val color: String,
    @SerializedName("descricaoTipoDataCalendario")val type: String,
    @SerializedName("nomeProfessor")val professorName: String?,
    @SerializedName("descricaoSala")val classDescription: String?
) {
    fun toDomain(): SpecialDate {
        return SpecialDate(
            startDate = LocalDateTime.parse(startDate),
            endDate = LocalDateTime.parse(endDate),
            description = description,
            color = color,
            type = type,
            professorName = professorName,
            classDescription = classDescription
        )
    }
}