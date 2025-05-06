package com.adielcalixto.ifacademico.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adielcalixto.ifacademico.domain.entities.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
private data class SerializableSession(
    val cookies: Set<String>,
    val expiresAt: Long,
    val registration: String,
    val password: String
) {
    fun toDomain() = Session(cookies, expiresAt, registration, password)

    companion object {
        fun fromDomain(session: Session) = SerializableSession(
            session.cookies,
            session.expiresAt,
            session.registration,
            session.password
        )
    }
};

class SessionPreferencesImpl @Inject constructor(
    private val context: Context,
    private val encryptionUtil: EncryptionUtil
) : SessionPreferences {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

    override suspend fun saveCookies(expiresAt: Long, cookies: Set<String>) {
        val currentSession = getSession().first()
        saveSession(currentSession.copy(cookies = cookies, expiresAt = expiresAt))
    }

    override suspend fun saveUser(registration: String, password: String) {
        val currentSession = getSession().first()
        saveSession(currentSession.copy(registration = registration, password = password))
    }

    private suspend fun saveSession(session: Session) {
        val encodedSession = Json.encodeToString(SerializableSession.fromDomain(session))
        context.dataStore.edit { preferences ->
            preferences[DATA_KEY] =
                encryptionUtil.encrypt(encodedSession, ENCRYPTION_KEY_ALIAS).joinToString("|")
        }
    }

    override fun getSession(): Flow<Session> {
        val encryptedData = context.dataStore.data.map { preferences ->
            preferences[DATA_KEY] ?: ""
        }

        return flow {
            val data = encryptedData.first()

            if (data.isEmpty()) {
                emit(Session(emptySet(), 0, "", ""))
            } else {
                val decryptedString = encryptionUtil.decrypt(
                    data.split("|").map { it.toByte() }.toByteArray(),
                    ENCRYPTION_KEY_ALIAS
                )
                emit(Json.decodeFromString<SerializableSession>(decryptedString).toDomain())
            }
        }
    }

    override suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val DATA_KEY = stringPreferencesKey("data")
        private const val ENCRYPTION_KEY_ALIAS = "session"
    }
}
