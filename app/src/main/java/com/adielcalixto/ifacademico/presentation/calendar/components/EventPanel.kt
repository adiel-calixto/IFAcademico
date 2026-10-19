package com.adielcalixto.ifacademico.presentation.calendar.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme

@Composable
fun IFEventPanel(
    type: String, description: String, date: String, modifier: Modifier = Modifier,
    subject: String? = null, stage: String? = null, teacher: String? = null, room: String? = null,
) {
    Surface(modifier.fillMaxWidth(), color = IFTheme.colors.surface, shape = IFShapes.md,
        border = BorderStroke(1.dp, IFTheme.colors.outline)) {
        Column(Modifier.padding(IFSpacing.md), verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
            Text(type, style = IFTheme.typography.labelSm, color = IFTheme.colors.secondary)
            Text(description, style = IFTheme.typography.detailTitle, color = IFTheme.colors.onSurface)
            subject?.let { Text(it, style = IFTheme.typography.bodyMd) }
            stage?.let { Text(it, style = IFTheme.typography.labelSm, color = IFTheme.colors.secondary) }
            Text(date, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
            teacher?.let { Text(it, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary) }
            room?.let { Text(it, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary) }
        }
    }
}
