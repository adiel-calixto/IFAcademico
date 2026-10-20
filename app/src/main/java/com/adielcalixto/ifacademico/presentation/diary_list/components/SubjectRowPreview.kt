package com.adielcalixto.ifacademico.presentation.diary_list.components

import androidx.compose.runtime.Composable
import com.adielcalixto.ifacademico.presentation.components.Catalog
import com.adielcalixto.ifacademico.presentation.components.IFCatalogPreviews

@IFCatalogPreviews
@Composable
internal fun SubjectRowPreview() = Catalog {
    IFSubjectRow(IFSubjectRecord("Introduction to algorithms and computational thinking", "Professor Maria Costa", 4, 80), {})
}
