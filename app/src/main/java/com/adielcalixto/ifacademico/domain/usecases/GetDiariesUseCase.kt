package com.adielcalixto.ifacademico.domain.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.Diary

interface GetDiariesUseCase {
    suspend fun execute(registrationId: Int, periodNumber: Int, periodYear: Int): Result<List<Diary>, Error.DataError>
}