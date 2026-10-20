package com.adielcalixto.ifacademico.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adielcalixto.ifacademico.domain.entities.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

private val Context.sessionDataStore by preferencesDataStore(
    name = "session",
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
)

@Serializable
internal data class SerializableSession(
    val cookies: Set<String> = emptySet(),
    val expiresAt: Long = 0,
    val registration: String = "",
    val password: String = "",
    val loggedInAt: Long? = null,
    val schemaVersion: Int = 1,
) {
    fun toDomain() = if (schemaVersion == 2) {
        Session(cookies, 0, registration, password, loggedInAt)
    } else {
        // Legacy expiry used local time as UTC and was reset by arbitrary cookies.
        Session(registration = registration, password = password)
    }

    companion object {
        fun fromDomain(session: Session) = SerializableSession(
            cookies = session.cookies, registration = session.registration,
            password = session.password, loggedInAt = session.loggedInAt, schemaVersion = 2,
        )
    }
}

class SessionPreferencesImpl internal constructor(
    private val dataStore: DataStore<Preferences>,
    private val encryptionUtil: EncryptionUtil,
) : SessionPreferences {
    @Inject
    constructor(context: Context, encryptionUtil: EncryptionUtil) : this(
        context.sessionDataStore,
        encryptionUtil
    )

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    override fun getSession(): Flow<Session> = flow {
        // I/O errors are recoverable. Corrupt ciphertext/JSON must be removed, not retried forever.
        val data = dataStore.data.first()[DATA_KEY]
        val session = if (data == null) Session() else try {
            val plaintext = encryptionUtil.decrypt(
                data.split("|").map { it.toByte() }.toByteArray(), ENCRYPTION_KEY_ALIAS,
            )
            json.decodeFromString<SerializableSession>(plaintext).toDomain()
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            clearSession()
            Session()
        }
        emit(session)
    }

    override suspend fun replaceSession(session: Session) {
        dataStore.edit { preferences ->
            val plaintext = json.encodeToString(SerializableSession.fromDomain(session))
            preferences[DATA_KEY] =
                encryptionUtil.encrypt(plaintext, ENCRYPTION_KEY_ALIAS).joinToString("|")
        }
    }

    override suspend fun clearSession() {
        dataStore.edit { it.remove(DATA_KEY) }
    }

    companion object {
        private val DATA_KEY = stringPreferencesKey("data")
        private const val ENCRYPTION_KEY_ALIAS = "session"
    }
}
