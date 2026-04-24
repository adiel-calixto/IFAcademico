package com.adielcalixto.ifacademico.data.remote

import android.util.Log
import com.adielcalixto.ifacademico.BuildConfig
import com.adielcalixto.ifacademico.data.local.SettingsPreferences
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

data class ReleaseInfo(
    val latestVersion: String,
    val releaseUrl: String,
    val releaseNotes: String,
)

sealed class UpdateCheckResult {
    data class UpdateAvailable(val info: ReleaseInfo) : UpdateCheckResult()
    data object UpToDate : UpdateCheckResult()
}

@Singleton
class UpdateChecker @Inject constructor(
    private val gitHubAPI: GitHubAPI,
    private val updatePreferences: SettingsPreferences
) {
    suspend fun check(): UpdateCheckResult {
        val isEnabled = updatePreferences.isUpdateCheckEnabled.first()
        if (!isEnabled) return UpdateCheckResult.UpToDate

        val skipped = updatePreferences.skippedVersion.first()

        return try {
            val response = gitHubAPI.getLatestRelease()
            if (!response.isSuccessful) return UpdateCheckResult.UpToDate

            val release = response.body() ?: return UpdateCheckResult.UpToDate
            val latest = release.tagName.trimStart('v')
            val current = BuildConfig.VERSION_NAME.trimStart('v')

            if (isNewer(latest, current) && skipped != latest) {
                UpdateCheckResult.UpdateAvailable(
                    ReleaseInfo(
                        latestVersion = latest,
                        releaseUrl = release.htmlUrl,
                        releaseNotes = release.body?.take(300) ?: "",
                    )
                )
            } else {
                UpdateCheckResult.UpToDate
            }
        } catch (e: Exception) {
            Log.i("CHECK_UPDATE", "Request Failed", e)
            UpdateCheckResult.UpToDate
        }
    }

    private fun isNewer(candidate: String, current: String): Boolean {
        fun parse(v: String) = v.split(".").map { it.toIntOrNull() ?: 0 }
        val c = parse(candidate)
        val cur = parse(current)
        val len = maxOf(c.size, cur.size)
        for (i in 0 until len) {
            val diff = (c.getOrElse(i) { 0 }) - (cur.getOrElse(i) { 0 })
            if (diff != 0) return diff > 0
        }
        return false
    }
}
