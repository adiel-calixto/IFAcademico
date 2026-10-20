package com.adielcalixto.ifacademico.data.remote.interceptors

import com.adielcalixto.ifacademico.data.session.SessionTicket
import okhttp3.Interceptor
import okhttp3.Response

/** Cookie scope is evaluated for the actual request URL; login has no ticket. */
class SessionCookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val cookies = request.tag(SessionTicket::class.java)?.cookies.orEmpty()
            .filter { it.expiresAt > System.currentTimeMillis() && it.matches(request.url) }
        return chain.proceed(request.newBuilder().apply {
            removeHeader("Cookie")
            if (cookies.isNotEmpty()) header(
                "Cookie",
                cookies.joinToString("; ") { "${it.name}=${it.value}" })
        }.build())
    }
}
