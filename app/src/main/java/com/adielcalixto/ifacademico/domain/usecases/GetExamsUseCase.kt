package com.adielcalixto.ifacademico.domain.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.Exam

interface GetExamsUseCase {
    suspend fun execute(diaryId: Int, registrationId: Int): Result<List<Exam>, Error.DataError>
}