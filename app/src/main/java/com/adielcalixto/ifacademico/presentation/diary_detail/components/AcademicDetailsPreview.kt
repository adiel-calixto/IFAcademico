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
    IFAttendanceSummary(IFAttendanceRecord(2, 20, 1, false, 80, 40, 20))
    IFAttendanceSummary(IFAttendanceRecord(18, 20, 1, true, 80, 80, 0))
    IFAttendanceSummary(IFAttendanceRecord(0, 0, 0, false, 0, 0, 0))
    IFAttendanceSummary(IFAttendanceRecord(0, 20, 0, false, 80, 0, 80))
    IFAttendanceSummary(IFAttendanceRecord(2, 20, 0, false, 80, 90, 10))
    IFAssessments(listOf(
        IFAssessmentStage("Stage 1", listOf(
            SampleAssessment.copy(formula = null),
            SampleAssessment.copy(description = "Practical assignment", acronym = "A2", grade = "0", formula = null),
            SampleAssessment.copy(description = "Stage average", acronym = "Average", date = null),
        )),
        IFAssessmentStage("Stage 2", listOf(SampleAssessment.copy(grade = null, date = null, formula = null))),
    ))
}
