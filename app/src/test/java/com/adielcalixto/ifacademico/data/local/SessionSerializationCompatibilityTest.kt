package com.adielcalixto.ifacademico.data.local

import com.adielcalixto.ifacademico.domain.entities.Session
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class SessionSerializationCompatibilityTest {
    @Test
    fun `legacy credentials migrate but old cookies and synthetic expiry are discarded`() {
        val legacy =
            """{"cookies":["session=fake","route=test"],"expiresAt":1792440000000,"registration":"test-student","password":"synthetic-password"}"""
        val session = Json.decodeFromString<SerializableSession>(legacy).toDomain()
        assertEquals("test-student", session.registration)
        assertEquals("synthetic-password", session.password)
        assertTrue(session.cookies.isEmpty())
        assertNull(session.loggedInAt)
        assertEquals(0L, session.expiresAt)
    }

    @Test
    fun `new schema round trips successful login time scoped cookies and credentials`() {
        val session = Session(
            cookies = setOf("session=fake; Path=/; Secure; HttpOnly"),
            registration = "test-student",
            password = "synthetic-password",
            loggedInAt = 1792440000000L
        )
        val encoded = Json.encodeToString(SerializableSession.fromDomain(session))
        assertEquals(session, Json.decodeFromString<SerializableSession>(encoded).toDomain())
    }

    @Test
    fun `unrecognized schema cannot restore cookies or login time`() {
        val future =
            """{"schemaVersion":10,"cookies":["session=old"],"loggedInAt":1792440000000,"registration":"test-student","password":"synthetic-password"}"""
        val session = Json.decodeFromString<SerializableSession>(future).toDomain()
        assertTrue(session.cookies.isEmpty())
        assertNull(session.loggedInAt)
    }
}
