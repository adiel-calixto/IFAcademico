package com.adielcalixto.ifacademico.data.local

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionSerializationCompatibilityTest {
    @Test
    fun `legacy session JSON retains cookies expiry and credentials`() {
        // Synthetic values in the format persisted before the dependency upgrade.
        val legacy = """{"cookies":["session=fake","route=test"],"expiresAt":1792440000000,"registration":"test-student","password":"synthetic-password"}"""

        val stored = Json.decodeFromString<SerializableSession>(legacy)
        val session = stored.toDomain()

        assertEquals(setOf("session=fake", "route=test"), session.cookies)
        assertEquals(1792440000000L, session.expiresAt)
        assertEquals("test-student", session.registration)
        assertEquals("synthetic-password", session.password)
        assertEquals(
            Json.parseToJsonElement(legacy),
            Json.parseToJsonElement(Json.encodeToString(SerializableSession.fromDomain(session)))
        )
    }

    @Test
    fun `empty session retains all persisted keys`() {
        val legacy = """{"cookies":[],"expiresAt":0,"registration":"","password":""}"""
        val stored = Json.decodeFromString<SerializableSession>(legacy)
        val encoded = Json.parseToJsonElement(Json.encodeToString(stored)).jsonObject

        assertEquals(setOf("cookies", "expiresAt", "registration", "password"), encoded.keys)
        assertEquals(Json.parseToJsonElement(legacy), encoded)
    }
}
