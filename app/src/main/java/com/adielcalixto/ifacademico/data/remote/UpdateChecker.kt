package com.adielcalixto.ifacademico.data.remote

import com.adielcalixto.ifacademico.data.Logger
import com.adielcalixto.ifacademico.data.local.SettingsPreferences
import com.adielcalixto.ifacademico.data.remote.dto.GitHubReleaseDTO
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

private const val RELEASE_NOTES_MAX_CHARS = 300

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
    private val updatePreferences: SettingsPreferences,
    private val logger: Logger,
    @Named("currentVersion") private val currentVersion: String,
) {
    suspend fun check(): UpdateCheckResult {
        if (!updatePreferences.isUpdateCheckEnabled.first()) {
            return UpdateCheckResult.UpToDate
        }

        return try {
            fetchLatestRelease()
        } catch (e: Exception) {
            logger.i(TAG, "Update check failed", e)
            UpdateCheckResult.UpToDate
        }
    }

    private suspend fun fetchLatestRelease(): UpdateCheckResult {
        val release = gitHubAPI.getLatestRelease()
            .takeIf { it.isSuccessful }
            ?.body()
            ?: run {
                logger.i(TAG, "Update check returned no usable response")
                return UpdateCheckResult.UpToDate
            }

        val latest = release.tagName.toCleanVersion()
        val versionToCompare = currentVersion.toCleanVersion()
        val skipped = updatePreferences.skippedVersion.first()

        return when {
            !latest.isNewerThan(versionToCompare) -> UpdateCheckResult.UpToDate
            latest == skipped -> UpdateCheckResult.UpToDate
            else -> UpdateCheckResult.UpdateAvailable(release.toReleaseInfo(latest))
        }
    }

    private fun GitHubReleaseDTO.toReleaseInfo(resolvedVersion: String) = ReleaseInfo(
        latestVersion = resolvedVersion,
        releaseUrl = htmlUrl,
        releaseNotes = body?.toTruncatedNotes() ?: "",
    )

    companion object {
        const val TAG = "UpdateChecker"
    }
}

private fun String.toCleanVersion() = trimStart('v')

private fun String.toTruncatedNotes() = take(RELEASE_NOTES_MAX_CHARS) // or trimToLastLine()

private fun String.isNewerThan(other: String): Boolean {
    val candidate = parseVersion()
    val current = other.parseVersion()
    val len = maxOf(candidate.size, current.size)
    for (i in 0 until len) {
        val diff = candidate.getOrElse(i) { 0 } - current.getOrElse(i) { 0 }
        if (diff != 0) return diff > 0
    }
    return false
}

private fun String.parseVersion() = split(".").map { part ->
    part.filter(Char::isDigit).toIntOrNull() ?: 0
}
