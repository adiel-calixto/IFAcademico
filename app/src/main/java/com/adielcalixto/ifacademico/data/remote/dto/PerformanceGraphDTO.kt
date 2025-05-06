package com.adielcalixto.ifacademico.data.remote.dto

import com.adielcalixto.ifacademico.domain.entities.PerformanceCoefficients
import com.google.gson.annotations.SerializedName

data class PerformanceGraphDTO(
    @SerializedName("coeficienteRendimento") val studentPerformance: Float,
    @SerializedName("coeficienteRendimentoTurma") val classPerformance: Float,
    @SerializedName("maiorCoeficienteRendimentoTurma") val highestClassPerformance: Float,
    @SerializedName("menorCoeficienteRendimentoTurma") val lowestClassPerformance: Float,
) {
    fun toDomain(): PerformanceCoefficients {
        return PerformanceCoefficients(
            studentPerformance,
            classPerformance,
            highestClassPerformance
        )
    }
}