package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.adielcalixto.ifacademico.presentation.IFTheme

@Composable
fun IFSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    detail: Boolean = false,
    textAlign: TextAlign? = null,
) {
    Text(title, modifier.semantics { heading() },
        style = if (detail) IFTheme.typography.detailTitle else IFTheme.typography.titleSm,
        color = IFTheme.colors.onSurface, textAlign = textAlign)
}
