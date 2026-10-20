package com.adielcalixto.ifacademico.presentation.components

import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.presentation.IFTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

@IFCatalogPreviews
@Composable
internal fun FeedbackPreview() = Catalog {
    IFErrorState("Unable to load your academic records. Check your connection and try again.", {})
    IFEmptyState("No classes scheduled for this day.")
    IFToast("Appearance saved")
    IFConfirmationContent("Close your session and hide your academic records?", "Log out", {}, {})
}

@IFCatalogPreviews
@Composable
internal fun ErrorPagePreview() {
    IFTheme {
        Surface(color = IFTheme.colors.background) {
            ErrorComponent(IOError, {})
        }
    }
}
