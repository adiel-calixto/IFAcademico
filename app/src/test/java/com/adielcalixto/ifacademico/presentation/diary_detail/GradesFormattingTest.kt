package com.adielcalixto.ifacademico.presentation.diary_detail

import com.adielcalixto.ifacademico.data.remote.dto.ExamDTO
import com.adielcalixto.ifacademico.domain.entities.Exam
import com.google.gson.Gson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
import java.util.Locale

class GradesFormattingTest {
    private val exam = Exam("Assessment", LocalDateTime.of(2026, 10, 19, 8, 0), null, 0f, 1, "A1", 10f, "1")
    private val json = """{"descricao":"Test","data":null,"formula":null,"ordem":1,"sigla":"A1","notaMaxima":10,"idEtapa":"1"}"""

    @Test
    fun `wire payload preserves zero null and omitted grades`() {
        val gson = Gson()
        assertNull(gson.fromJson(json, ExamDTO::class.java).toDomain().grade)
        assertFalse(gson.fromJson(json.dropLast(1) + ",\"nota\":null}", ExamDTO::class.java).toDomain().hasGrade)
        val zero = gson.fromJson(json.dropLast(1) + ",\"nota\":0}", ExamDTO::class.java).toDomain()
        assertEquals(0f, zero.grade)
        assertTrue(zero.hasGrade)
    }

    @Test
    fun `cached DTOs retain old numeric grades and new nullable grades`() {
        val dto = Gson().fromJson(json, ExamDTO::class.java)
        assertNull(Json.decodeFromString<ExamDTO>(Json.encodeToString(dto)).grade)
        val cached = """{"description":"Test","date":null,"formula":null,"grade":0,"order":1,"sigla":"A1","maxGrade":10,"stepId":"1"}"""
        assertEquals(0f, Json.decodeFromString<ExamDTO>(cached).toDomain().grade)
        assertNull(Json.decodeFromString<ExamDTO>(cached.replace("\"grade\":0,", "")).grade)
    }

    @Test
    fun `stages preserve API order with averages inline and missing data explicit`() {
        val records = listOf(
            exam.copy(order = 3, acronym = "Média", formula = "(A1+A2)/2", grade = 8.5f),
            exam.copy(order = 4, stepId = "2", date = null, grade = null),
            exam.copy(order = 2, description = "Second"), exam
        )
        val stages = assessmentStages(records, Locale.forLanguageTag("pt-BR")) { "Etapa $it" }
        assertEquals(listOf("Etapa 1", "Etapa 2"), stages.map { it.label })
        assertEquals(listOf("A1", "A1", "Média"), stages.first().assessments.map { it.acronym })
        assertEquals("0", stages.first().assessments.first().grade)
        assertEquals("8,5", stages.first().assessments.last().grade)
        assertEquals("(A1+A2)/2", stages.first().assessments.last().formula)
        assertNull(stages.last().assessments.single().grade)
        assertNull(stages.last().assessments.single().date)
        val english = assessmentStages(listOf(exam.copy(grade = 8.5f)), Locale.US) { it }
        assertEquals("8.5", english.single().assessments.single().grade)
        assertEquals("10/19/26", english.single().assessments.single().date)
    }

    @Test
    fun `warning starts at eighty percent and requires positive allowance`() {
        assertFalse(hasLowAllowance(15, 20))
        assertTrue(hasLowAllowance(16, 20))
        assertTrue(hasLowAllowance(25, 20))
        assertFalse(hasLowAllowance(0, 0))
        assertFalse(hasLowAllowance(1, 0))
    }
}
