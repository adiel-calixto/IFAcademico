package com.adielcalixto.ifacademico.data.remote

import com.adielcalixto.ifacademico.data.remote.dto.GitHubReleaseDTO
import retrofit2.Response
import retrofit2.http.GET

interface GitHubAPI {
    @GET("repos/adiel-calixto/ifacademico/releases/latest")
    suspend fun getLatestRelease(): Response<GitHubReleaseDTO>

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}
