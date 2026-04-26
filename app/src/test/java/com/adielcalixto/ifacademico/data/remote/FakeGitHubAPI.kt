package com.adielcalixto.ifacademico.data.remote

import com.adielcalixto.ifacademico.data.remote.dto.GitHubReleaseDTO
import retrofit2.Response

class FakeGitHubAPI : GitHubAPI {
    var responseToReturn: Response<GitHubReleaseDTO>? = null
    var shouldThrow: Boolean = false

    override suspend fun getLatestRelease(): Response<GitHubReleaseDTO> {
        if (shouldThrow) throw RuntimeException("Network error")
        return responseToReturn ?: throw IllegalStateException("Response not configured")
    }

    fun reset() {
        responseToReturn = null
        shouldThrow = false
    }
}
