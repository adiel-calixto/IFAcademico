package com.adielcalixto.ifacademico.presentation.calendar.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.adielcalixto.ifacademico.presentation.components.Catalog
import com.adielcalixto.ifacademico.presentation.components.IFCatalogPreviews
import com.adielcalixto.ifacademico.presentation.components.IFEmptyState
import com.adielcalixto.ifacademico.presentation.components.IFSectionTitle
import java.time.LocalDate
import java.time.YearMonth

@IFCatalogPreviews
@Composable
internal fun CalendarPreview() = Catalog {
    var month by remember { mutableStateOf(YearMonth.of(2026, 10)) }
    IFMonthGrid(month, LocalDate.of(2026, 10, 19),
        setOf(LocalDate.of(2026, 10, 19), LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 21)),
        { month = month.minusMonths(1) }, { month = month.plusMonths(1) })
    IFSectionTitle("Special dates")
    IFEventPanel("Assessment", "Written assessment covering algorithms and complexity", "21/10/2026",
        subject = "Algorithms", stage = "Stage 1", teacher = "Professor Maria Costa", room = "Room 204")
    IFEventPanel("Academic event", "Science and technology week", "19/10/2026 – 23/10/2026")
    IFEmptyState("No special dates listed for this month.")
}
