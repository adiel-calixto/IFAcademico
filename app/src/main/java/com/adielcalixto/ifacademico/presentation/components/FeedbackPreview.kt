package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

@IFCatalogPreviews
@Composable
internal fun FeedbackPreview() = Catalog {
    IFLoadingPlaceholder()
    IFErrorState("Unable to load your academic records. Check your connection and try again.", {})
    IFEmptyState("No classes scheduled for this day.")
    IFToast("Appearance saved")
    IFConfirmationContent("Close your session and hide your academic records?", "Log out", {}, {})
}
