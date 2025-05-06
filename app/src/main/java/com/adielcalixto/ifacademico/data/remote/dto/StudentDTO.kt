package com.adielcalixto.ifacademico.data.remote.dto

import com.adielcalixto.ifacademico.domain.entities.Student
import com.google.gson.annotations.SerializedName

data class StudentDTO(
    @SerializedName("tipoUsuario") val userType: Int,
    @SerializedName("idPessoa") val personId: Int,
    @SerializedName("idMatricula") val registrationId: Int,
    @SerializedName("nomePessoa") val name: String,
    @SerializedName("redefinicaoSenhaObrigatoria") val shouldChangePassword: Int,
    @SerializedName("idCurso") val courseId: Int,
    @SerializedName("descCurso") val courseName: String
) {

    fun toDomainEntity(): Student {
        return Student(name, registrationId, personId, courseName)
    }
}
