package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.usecases.GetSessionUseCase
import com.adielcalixto.ifacademico.domain.usecases.LoginUseCase
import com.adielcalixto.ifacademico.domain.usecases.LogoutUseCase
import com.adielcalixto.ifacademico.domain.usecases.RefreshSessionUseCase
import javax.inject.Inject

class RefreshSessionUseCaseImpl @Inject constructor(
    private val getSessionUseCase: GetSessionUseCase,
    private val loginUseCase: LoginUseCase,
    private val cacheService: CacheService
) : RefreshSessionUseCase {
    override suspend fun execute(): Result<Unit, Error.DataError> {
        val session = getSessionUseCase.execute()

        return when (val result = loginUseCase.execute(session.registration, session.password)) {
            is Result.Error -> {
                return when (result.error) {
                    IOError -> Result.Error(IOError)
                    else -> Result.Error(UnknownError)
                }
            }

            is Result.Success -> {
                cacheService.clearCache()
                Result.Success(Unit)
            }
        }
    }
}