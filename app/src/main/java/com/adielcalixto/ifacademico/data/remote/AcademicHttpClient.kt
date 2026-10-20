package com.adielcalixto.ifacademico.data.remote

import com.adielcalixto.ifacademico.data.remote.interceptors.SessionCookiesInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/** Shared by production wiring and HTTP regression tests. */
fun academicHttpClient(): OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .writeTimeout(20, TimeUnit.SECONDS)
    .callTimeout(45, TimeUnit.SECONDS)
    .retryOnConnectionFailure(false)
    .followRedirects(false)
    .followSslRedirects(false)
    .addInterceptor(SessionCookiesInterceptor())
    .build()
