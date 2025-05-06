package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.LoginUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeLoginUseCase @Inject constructor(): LoginUseCase {
    override suspend fun execute(
        registration: String,
        password: String
    ): Result<Unit, Error.DataError> {
        delay(1)
        return Result.Success(Unit)
    }
}