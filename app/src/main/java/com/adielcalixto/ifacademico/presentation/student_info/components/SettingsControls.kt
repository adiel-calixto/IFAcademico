package com.adielcalixto.ifacademico.presentation.student_info.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFMotion
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.Theme
import com.adielcalixto.ifacademico.presentation.asUiText
import com.adielcalixto.ifacademico.presentation.components.IFIcon

@Composable
fun IFUpdateSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.check_for_updates),
    enabled: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val thumbOffset by animateDpAsState(
        if (checked) 22.dp else 2.dp, tween(IFMotion.toggleMillis), label = "switch thumb",
    )
    Row(
        modifier.clip(IFShapes.md)
            .then(if (focused) Modifier.border(2.dp, IFTheme.colors.primary, IFShapes.md) else Modifier)
            .toggleable(checked, enabled = enabled, role = Role.Switch,
                interactionSource = interaction, indication = ripple(), onValueChange = onCheckedChange)
            .semantics { contentDescription = label }
            .heightIn(min = IFSize.touchTarget).padding(horizontal = IFSpacing.micro),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(IFSpacing.xs),
    ) {
        Text(stringResource(if (checked) R.string.setting_on else R.string.setting_off),
            style = IFTheme.typography.labelMd, color = IFTheme.colors.onSurface)
        Box(Modifier.size(IFSize.switchWidth, IFSize.switchHeight).clip(IFShapes.full)
            .background(if (checked && enabled) IFTheme.colors.primary else IFTheme.colors.inputOutline)) {
            Box(Modifier.offset { IntOffset(thumbOffset.roundToPx(), 2.dp.roundToPx()) }.size(20.dp).clip(IFShapes.full)
                .background(if (checked && enabled) IFTheme.colors.onPrimary else IFTheme.colors.surface))
        }
    }
}

@Composable
fun IFThemeSelector(
    theme: Theme,
    onThemeChange: (Theme) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    Box(modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.heightIn(min = IFSize.themeSelect)
                .then(if (compact) Modifier else Modifier.fillMaxWidth()),
            shape = IFShapes.sm,
            border = BorderStroke(if (focused) 2.dp else 1.dp,
                if (focused) IFTheme.colors.primary else IFTheme.colors.inputOutline),
            interactionSource = interaction,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = IFTheme.colors.surface, contentColor = IFTheme.colors.onSurface),
        ) {
            Text(
                if (compact) theme.asUiText().asString()
                else stringResource(R.string.theme) + ": " + theme.asUiText().asString(),
                if (compact) Modifier.padding(end = IFSpacing.xs) else Modifier.weight(1f),
                style = IFTheme.typography.labelMd,
            )
            Icon(IFIcon.Down.vector, null, Modifier.size(IFSize.icon))
        }
        DropdownMenu(expanded, { expanded = false }, containerColor = IFTheme.colors.surface) {
            Theme.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.asUiText().asString()) },
                    onClick = { expanded = false; onThemeChange(option) },
                    modifier = Modifier.semantics { this.selected = option == theme },
                )
            }
        }
    }
}
