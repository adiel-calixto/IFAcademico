package com.adielcalixto.ifacademico.data.remote.interceptors

import com.adielcalixto.ifacademico.domain.entities.Session
import com.adielcalixto.ifacademico.domain.usecases.SaveSessionUseCase
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response


class CookieInterceptor(private val saveSessionUseCase: SaveSessionUseCase) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val cookies = response.headers("Set-Cookie")

        if (cookies.isNotEmpty()) {
            val cookiesTransformed = cookies.map{ it.split(";")[0] }
            runBlocking { saveSessionUseCase.execute(Session(cookiesTransformed.toSet(), registration = "", password = "")) }
        }

        return response
    }
}
