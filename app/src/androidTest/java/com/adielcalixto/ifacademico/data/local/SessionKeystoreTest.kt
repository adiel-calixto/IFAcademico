package com.adielcalixto.ifacademico.data.local

import org.junit.Assert.*
import org.junit.Test
import java.security.KeyStore
import java.util.UUID

class SessionKeystoreTest {
    @Test
    fun encryptedSessionSurvivesASecondCodecAndMissingKeyCannotDecodeIt() {
        val alias = "session-test-${UUID.randomUUID()}"
        val keystore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val synthetic =
            """{"schemaVersion":2,"registration":"synthetic-student","password":"synthetic-password"}"""
        try {
            val ciphertext = EncryptionUtil().encrypt(synthetic, alias)
            assertFalse(ciphertext.contentEquals(synthetic.toByteArray()))
            assertEquals(synthetic, EncryptionUtil().decrypt(ciphertext, alias))
            keystore.deleteEntry(alias)
            var rejected = false
            try {
                EncryptionUtil().decrypt(ciphertext, alias)
            } catch (_: Exception) {
                rejected = true
            }
            assertTrue(rejected)
        } finally {
            keystore.deleteEntry(alias)
        }
    }
}
