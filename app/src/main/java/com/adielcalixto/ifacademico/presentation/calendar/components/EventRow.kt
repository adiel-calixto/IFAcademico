package com.adielcalixto.ifacademico.presentation.calendar.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme

@Composable
fun IFEventRow(
    description: String,
    date: String,
    modifier: Modifier = Modifier,
    dateDescription: String = date,
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(vertical = IFSpacing.micro),
            horizontalArrangement = Arrangement.spacedBy(IFSpacing.xs),
        ) {
            Text(
                date,
                modifier = Modifier.weight(0.3f).semantics { contentDescription = dateDescription },
                style = IFTheme.typography.bodySm,
                color = IFTheme.colors.secondary,
                textAlign = TextAlign.Center,
            )
            Text(
                description,
                modifier = Modifier.weight(1f),
                style = IFTheme.typography.bodySm,
                color = IFTheme.colors.onSurface,
            )
        }
        HorizontalDivider(color = IFTheme.colors.outline)
    }
}
