package com.adielcalixto.ifacademico.data.remote.interceptors

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.observers.UnauthorizedApiErrorObserver
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class UnauthorizedErrorInterceptor @Inject constructor(
    private val unauthorizedApiErrorObserver: UnauthorizedApiErrorObserver,
    private val cacheService: CacheService
) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401) {
            unauthorizedApiErrorObserver.notifyError()
            cacheService.clearCache()
        }

        return response
    }
}
