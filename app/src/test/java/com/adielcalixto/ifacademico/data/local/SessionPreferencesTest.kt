package com.adielcalixto.ifacademico.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.adielcalixto.ifacademico.domain.entities.Session
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.security.GeneralSecurityException

class SessionPreferencesTest {
    @get:Rule
    val temporary = TemporaryFolder()
    private val encryption = mockk<EncryptionUtil>()

    @Test
    fun `encrypted legacy credentials migrate and new authentication persists coherently`() =
        runTest {
            val dataStore = PreferenceDataStoreFactory.create(
                scope = backgroundScope,
                produceFile = { temporary.newFile("session.preferences_pb").apply { delete() } })
            every { encryption.decrypt(any(), "session") } answers {
                firstArg<ByteArray>().toString(
                    Charsets.UTF_8
                )
            }
            every {
                encryption.encrypt(
                    any(),
                    "session"
                )
            } answers { firstArg<String>().toByteArray() }
            val legacy =
                """{"cookies":["session=legacy"],"expiresAt":1792440000000,"registration":"student","password":"password"}"""
            dataStore.edit {
                it[stringPreferencesKey("data")] = legacy.toByteArray().joinToString("|")
            }
            val preferences = SessionPreferencesImpl(dataStore, encryption)
            val migrated = preferences.getSession().first()
            assertEquals("student", migrated.registration)
            assertEquals("password", migrated.password)
            assertTrue(migrated.cookies.isEmpty())
            assertNull(migrated.loggedInAt)
            val authenticated =
                migrated.copy(cookies = setOf("session=new; Path=/"), loggedInAt = 1792440000000)
            preferences.replaceSession(authenticated)
            assertEquals(authenticated, preferences.getSession().first())
            preferences.clearSession()
            assertEquals(Session(), preferences.getSession().first())
            verify { encryption.encrypt(any(), "session") }
        }

    @Test
    fun `malformed plaintext ciphertext and missing key reset storage to a usable login record`() =
        runTest {
            for ((index, stored) in listOf("not-byte-data", "1|2|3", "4|5|6").withIndex()) {
                val file = temporary.newFile("corrupt-$index.preferences_pb").apply { delete() }
                val dataStore = PreferenceDataStoreFactory.create(
                    scope = backgroundScope,
                    produceFile = { file })
                every { encryption.decrypt(any(), "session") } answers {
                    if (index == 1) "{invalid json" else throw GeneralSecurityException("key unavailable")
                }
                dataStore.edit { it[stringPreferencesKey("data")] = stored }
                val preferences = SessionPreferencesImpl(dataStore, encryption)
                assertEquals(Session(), preferences.getSession().first())
                assertNull(dataStore.data.first()[stringPreferencesKey("data")])
            }
        }
}
