package com.adielcalixto.ifacademico.data.remote.dto

import com.adielcalixto.ifacademico.domain.entities.Period
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PeriodDTO(
    @SerializedName("anoLetivo") val year: Int,
    @SerializedName("periodoLetivo") val number: Int
) {
    fun toDomain(): Period {
        return Period(
            year,
            number,
        )
    }
}
