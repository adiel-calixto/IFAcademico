package com.adielcalixto.ifacademico.data.remote.dto

import com.adielcalixto.ifacademico.domain.entities.Exam
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ExamDTO(
    @SerializedName("descricao") val description: String,
    @SerializedName("data") val date: String?,
    @SerializedName("formula") val formula: String?,
    @SerializedName("nota") val grade: Float,
    @SerializedName("ordem") val order: Int,
    @SerializedName("sigla") val sigla: String,
    @SerializedName("notaMaxima") val maxGrade: Float,
    @SerializedName("idEtapa") val stepId: String,
) {
    fun toDomain(): Exam {
        return Exam(
            description = description,
            date = if(date != null) LocalDateTime.parse(date) else null,
            formula = formula,
            grade = grade,
            order = order,
            acronym = sigla,
            maxGrade = maxGrade,
            stepId = stepId
        )
    }
}
