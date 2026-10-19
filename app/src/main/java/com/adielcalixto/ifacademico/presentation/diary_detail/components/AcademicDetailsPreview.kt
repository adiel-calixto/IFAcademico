package com.adielcalixto.ifacademico.presentation.diary_detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.adielcalixto.ifacademico.presentation.components.Catalog
import com.adielcalixto.ifacademico.presentation.components.IFCatalogPreviews

private val SampleAssessment = IFAssessmentRecord(
    "Written assessment covering algorithms and complexity", "A1", "21/10/2026", "8.5", "10", "(A1 + A2) / 2",
)

@IFCatalogPreviews
@Composable
internal fun AcademicDetailsPreview() = Catalog {
    IFAttendanceSummary(IFAttendanceRecord(2, 20, 18, 1, false))
    IFAttendanceSummary(IFAttendanceRecord(18, 20, 2, 1, true))
    IFAttendanceSummary(IFAttendanceRecord(0, 0, 0, 0, false))
    IFAssessments(listOf(
        IFAssessmentStage("Stage 1", listOf(
            SampleAssessment.copy(formula = null),
            SampleAssessment.copy(description = "Practical assignment", acronym = "A2", grade = "0", formula = null),
            SampleAssessment.copy(description = "Stage average", acronym = "Average", date = null),
        )),
        IFAssessmentStage("Stage 2", listOf(SampleAssessment.copy(grade = null, date = null, formula = null))),
    ))
}
