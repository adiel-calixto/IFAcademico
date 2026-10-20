package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme

@Composable
fun IFRecord(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(vertical = IFSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
            Text(label, Modifier.weight(1f), style = IFTheme.typography.bodySm,
                color = IFTheme.colors.secondary)
            Text(value, Modifier.weight(1.4f), style = IFTheme.typography.bodySm,
                color = IFTheme.colors.onSurface, textAlign = TextAlign.End)
        }
        HorizontalDivider(color = IFTheme.colors.outline)
    }
}

@Composable
fun IFNotice(text: String, modifier: Modifier = Modifier) {
    Surface(modifier, color = IFTheme.colors.notice, contentColor = IFTheme.colors.onNotice, shape = IFShapes.md) {
        Row(Modifier.padding(IFSpacing.md), horizontalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
            Icon(IFIcon.Warning.vector, null, Modifier.size(IFSize.icon))
            Text(text, style = IFTheme.typography.bodySm)
        }
    }
}
