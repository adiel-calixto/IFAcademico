package com.adielcalixto.ifacademico.presentation.student_info.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme

@Composable
fun IFInitialsAvatar(name: String, modifier: Modifier = Modifier) {
    val initials = name.trim().split(Regex("""\s+""")).filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.take(1).uppercase() }
    Box(modifier.size(IFSize.avatar).clip(CircleShape).background(IFTheme.colors.surfaceSubtle),
        contentAlignment = Alignment.Center) {
        Text(initials, style = IFTheme.typography.titleMd, color = IFTheme.colors.primary)
    }
}

@Composable
fun IFProfileIdentity(name: String, course: String, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
        IFInitialsAvatar(name)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
            Text(name, style = IFTheme.typography.titleLg, color = IFTheme.colors.onSurface)
            Text(course, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
        }
    }
}

@Composable
fun IFSettingsRow(label: String, helper: String?, modifier: Modifier = Modifier, control: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
        Text(label, style = IFTheme.typography.labelMd, color = IFTheme.colors.onSurface)
        helper?.let { Text(it, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary) }
        control()
    }
}
