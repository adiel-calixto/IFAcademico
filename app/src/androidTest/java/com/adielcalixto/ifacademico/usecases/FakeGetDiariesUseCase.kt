package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.Diary
import com.adielcalixto.ifacademico.domain.usecases.GetDiariesUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeGetDiariesUseCase @Inject constructor(): GetDiariesUseCase {
    override suspend fun execute(
        registrationId: Int,
        periodNumber: Int,
        periodYear: Int
    ): Result<List<Diary>, Error.DataError> {
        delay(1)
        return Result.Success(emptyList())
    }
}