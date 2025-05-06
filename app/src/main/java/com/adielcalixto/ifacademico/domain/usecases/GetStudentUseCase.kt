package com.adielcalixto.ifacademico.domain.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.Student

interface GetStudentUseCase {
    suspend fun execute(): Result<Student, Error.DataError>
}