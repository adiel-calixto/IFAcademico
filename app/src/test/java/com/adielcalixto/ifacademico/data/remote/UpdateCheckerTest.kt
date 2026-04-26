package com.adielcalixto.ifacademico.data.remote

import com.adielcalixto.ifacademico.data.Logger
import com.adielcalixto.ifacademico.data.local.SettingsPreferences
import com.adielcalixto.ifacademico.data.remote.dto.GitHubReleaseDTO
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import kotlinx.coroutines.test.runTest
import retrofit2.Response

class UpdateCheckerTest {

    private lateinit var gitHubAPI: GitHubAPI
    private lateinit var settings: SettingsPreferences
    private lateinit var logger: Logger
    private lateinit var updateChecker: UpdateChecker

    @Before
    fun setup() {
        gitHubAPI = mockk(relaxed = true)
        settings = mockk(relaxed = true)
        logger = mockk(relaxed = true)
        updateChecker = UpdateChecker(gitHubAPI, settings, logger, "2.0.0")
    }

    @Test
    fun `GIVEN update check disabled WHEN check THEN return UpToDate`() = runTest {
        every { settings.isUpdateCheckEnabled } returns flowOf(false)

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpToDate)
    }

    @Test
    fun `GIVEN API throws exception WHEN check THEN return UpToDate`() = runTest {
        every { settings.isUpdateCheckEnabled } returns flowOf(true)
        coEvery { gitHubAPI.getLatestRelease() } throws RuntimeException("Network error")

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpToDate)
    }

    @Test
    fun `GIVEN API response not successful WHEN check THEN return UpToDate`() = runTest {
        every { settings.isUpdateCheckEnabled } returns flowOf(true)
        every { settings.skippedVersion } returns flowOf(null)
        val failedResponse = mockResponse(body = null, isSuccessful = false)
        coEvery { gitHubAPI.getLatestRelease() } returns failedResponse

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpToDate)
    }

    @Test
    fun `GIVEN latest version same as current WHEN check THEN return UpToDate`() = runTest {
        every { settings.isUpdateCheckEnabled } returns flowOf(true)
        every { settings.skippedVersion } returns flowOf(null)
        val response = mockResponse(
            body = createRelease(tagName = "v2.0.0"),
            isSuccessful = true
        )
        coEvery { gitHubAPI.getLatestRelease() } returns response

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpToDate)
    }

    @Test
    fun `GIVEN latest version older than current WHEN check THEN return UpToDate`() = runTest {
        every { settings.isUpdateCheckEnabled } returns flowOf(true)
        every { settings.skippedVersion } returns flowOf(null)
        val response = mockResponse(
            body = createRelease(tagName = "v1.9.0"),
            isSuccessful = true
        )
        coEvery { gitHubAPI.getLatestRelease() } returns response

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpToDate)
    }

    @Test
    fun `GIVEN latest version newer AND not skipped WHEN check THEN return UpdateAvailable`() = runTest {
        every { settings.isUpdateCheckEnabled } returns flowOf(true)
        every { settings.skippedVersion } returns flowOf(null)
        val response = mockResponse(
            body = createRelease(tagName = "v2.1.0"),
            isSuccessful = true
        )
        coEvery { gitHubAPI.getLatestRelease() } returns response

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpdateAvailable)
        val updateResult = result as UpdateCheckResult.UpdateAvailable
        assertEquals("2.1.0", updateResult.info.latestVersion)
        assertEquals("https://github.com/adiel-calixto/ifacademico/releases/tag/v2.1.0", updateResult.info.releaseUrl)
    }

    @Test
    fun `GIVEN latest version same as skipped WHEN check THEN return UpToDate`() = runTest {
        every { settings.isUpdateCheckEnabled } returns flowOf(true)
        every { settings.skippedVersion } returns flowOf("2.1.0")
        val response = mockResponse(
            body = createRelease(tagName = "v2.1.0"),
            isSuccessful = true
        )
        coEvery { gitHubAPI.getLatestRelease() } returns response

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpToDate)
    }

    @Test
    fun `GIVEN version with v prefix WHEN check THEN strip prefix correctly`() = runTest {
        every { settings.isUpdateCheckEnabled } returns flowOf(true)
        every { settings.skippedVersion } returns flowOf(null)
        val response = mockResponse(
            body = createRelease(tagName = "v3.0.0"),
            isSuccessful = true
        )
        coEvery { gitHubAPI.getLatestRelease() } returns response

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpdateAvailable)
        val updateResult = result as UpdateCheckResult.UpdateAvailable
        assertEquals("3.0.0", updateResult.info.latestVersion)
    }

    @Test
    fun `GIVEN version without v prefix WHEN check THEN handle correctly`() = runTest {
        every { settings.isUpdateCheckEnabled } returns flowOf(true)
        every { settings.skippedVersion } returns flowOf(null)
        val response = mockResponse(
            body = createRelease(tagName = "3.0.0"),
            isSuccessful = true
        )
        coEvery { gitHubAPI.getLatestRelease() } returns response

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpdateAvailable)
        val updateResult = result as UpdateCheckResult.UpdateAvailable
        assertEquals("3.0.0", updateResult.info.latestVersion)
    }

    @Test
    fun `GIVEN release notes exceed max chars WHEN check THEN truncate to limit`() = runTest {
        val longNotes = "A".repeat(500)
        every { settings.isUpdateCheckEnabled } returns flowOf(true)
        every { settings.skippedVersion } returns flowOf(null)
        val response = mockResponse(
            body = createRelease(tagName = "v2.1.0", body = longNotes),
            isSuccessful = true
        )
        coEvery { gitHubAPI.getLatestRelease() } returns response

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpdateAvailable)
        val updateResult = result as UpdateCheckResult.UpdateAvailable
        assertEquals(300, updateResult.info.releaseNotes.length)
    }

    @Test
    fun `GIVEN release notes empty WHEN check THEN return empty notes`() = runTest {
        every { settings.isUpdateCheckEnabled } returns flowOf(true)
        every { settings.skippedVersion } returns flowOf(null)
        val response = mockResponse(
            body = createRelease(tagName = "v2.1.0", body = null),
            isSuccessful = true
        )
        coEvery { gitHubAPI.getLatestRelease() } returns response

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpdateAvailable)
        val updateResult = result as UpdateCheckResult.UpdateAvailable
        assertEquals("", updateResult.info.releaseNotes)
    }

    @Test
    fun `GIVEN minor version bump WHEN check THEN detect update`() = runTest {
        every { settings.isUpdateCheckEnabled } returns flowOf(true)
        every { settings.skippedVersion } returns flowOf(null)
        val response = mockResponse(
            body = createRelease(tagName = "v2.0.1"),
            isSuccessful = true
        )
        coEvery { gitHubAPI.getLatestRelease() } returns response

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpdateAvailable)
    }

    @Test
    fun `GIVEN patch version bump WHEN check THEN detect update`() = runTest {
        every { settings.isUpdateCheckEnabled } returns flowOf(true)
        every { settings.skippedVersion } returns flowOf(null)
        val response = mockResponse(
            body = createRelease(tagName = "v2.0.0.1"),
            isSuccessful = true
        )
        coEvery { gitHubAPI.getLatestRelease() } returns response

        val result = updateChecker.check()

        assertTrue(result is UpdateCheckResult.UpdateAvailable)
    }

    private fun createRelease(tagName: String, body: String? = "Release notes"): GitHubReleaseDTO {
        return GitHubReleaseDTO(
            tagName = tagName,
            name = "Release $tagName",
            body = body,
            htmlUrl = "https://github.com/adiel-calixto/ifacademico/releases/tag/$tagName"
        )
    }

    private fun mockResponse(body: GitHubReleaseDTO?, isSuccessful: Boolean): Response<GitHubReleaseDTO> {
        return mockk {
            every { this@mockk.isSuccessful } returns isSuccessful
            every { this@mockk.body() } returns body
            every { this@mockk.code() } returns if (isSuccessful) 200 else 404
            every { this@mockk.message() } returns if (isSuccessful) "OK" else "Not Found"
        }
    }
}
