package com.adielcalixto.ifacademico.data.usecases;

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.local.SessionPreferences
import com.adielcalixto.ifacademico.domain.usecases.LogoutUseCase
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class LogoutUseCaseImpl @Inject constructor(
    private val sessionPreferences: SessionPreferences,
    private val cacheService: CacheService
) : LogoutUseCase {
    override fun execute() {
        runBlocking {
            cacheService.clearCache()
            sessionPreferences.clearSession()
        }
    }
}
