package com.adielcalixto.ifacademico.presentation.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.adielcalixto.ifacademico.presentation.components.Catalog
import com.adielcalixto.ifacademico.presentation.components.IFCatalogPreviews
import com.adielcalixto.ifacademico.presentation.components.IFSectionTitle

private val SampleClass = IFClassRecord(
    "Introduction to algorithms and computational thinking", "08:00", "09:40", "Room 204", "Professor Maria Costa",
    finished = true, period = "2026 · 2",
)
@IFCatalogPreviews
@Composable
internal fun AcademicRowsPreview() = Catalog {
    IFCoefficientPanel("8.72", "7.94")
    IFSectionTitle("Today’s classes")
    IFClassRow(SampleClass.copy(teacher = null), {})
    IFClassRow(SampleClass.copy(subject = "Databases", teacher = null, finished = false), {})
    IFClassDetail(SampleClass)
    IFTimetableContent("2026 · 2", listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"), 2, listOf(SampleClass), {})
}
