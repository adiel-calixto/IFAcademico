package com.adielcalixto.ifacademico.data.remote.interceptors

import com.adielcalixto.ifacademico.domain.usecases.GetSessionUseCase
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AddCookiesInterceptor(private val getSessionUseCase: GetSessionUseCase) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val session = runBlocking { getSessionUseCase.execute() }
        val cookies:Set<String> = session.cookies

        val requestWithCookies = originalRequest.newBuilder()
            .apply {
                if (cookies.isNotEmpty()) {
                    val cookieHeader = cookies.joinToString("; ")
                    addHeader("Cookie", cookieHeader)
                }
            }
            .build()

        return chain.proceed(requestWithCookies)
    }
}