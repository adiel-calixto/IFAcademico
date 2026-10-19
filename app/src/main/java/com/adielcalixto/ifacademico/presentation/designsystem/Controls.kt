package com.adielcalixto.ifacademico.presentation.designsystem

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.Theme
import com.adielcalixto.ifacademico.presentation.asUiText

@Composable
fun IFButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    destructive: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    val c = IFTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val hovered by interaction.collectIsHoveredAsState()
    val pressed by interaction.collectIsPressedAsState()
    val normal = if (destructive) c.error else c.primary
    Button(
        onClick, modifier.heightIn(min = IFSize.button), enabled,
        shape = IFShapes.md,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (!destructive && (hovered || pressed)) c.primaryHover else normal,
            contentColor = if (destructive) c.background else c.onPrimary,
            disabledContainerColor = c.surfaceSubtle, disabledContentColor = c.secondary,
        ),
        border = if (focused) BorderStroke(2.dp, c.onSurface) else null,
        contentPadding = PaddingValues(horizontal = IFSpacing.md, vertical = IFSpacing.sm),
        interactionSource = interaction,
        content = { ProvideTextStyle(IFTheme.typography.labelMd) { content() } },
    )
}

@Composable
fun IFTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    destructive: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(IFSpacing.sm),
    content: @Composable RowScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val hovered by interaction.collectIsHoveredAsState()
    TextButton(
        onClick, modifier.heightIn(min = IFSize.touchTarget), enabled,
        shape = IFShapes.md,
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (destructive) IFTheme.colors.error else if (hovered) IFTheme.colors.primaryHover else IFTheme.colors.primary,
            disabledContentColor = IFTheme.colors.secondary,
        ),
        border = if (focused) BorderStroke(2.dp, IFTheme.colors.primary) else null,
        contentPadding = contentPadding, interactionSource = interaction,
        content = { ProvideTextStyle(IFTheme.typography.labelMd) { content() } },
    )
}

@Composable
fun IFIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    outlined: Boolean = false,
    content: @Composable () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val border = if (focused) IFTheme.colors.primary else IFTheme.colors.outline
    val hovered by interaction.collectIsHoveredAsState()
    val pressed by interaction.collectIsPressedAsState()
    Surface(
        onClick = onClick,
        modifier = modifier.sizeIn(minWidth = IFSize.touchTarget, minHeight = IFSize.touchTarget)
            .semantics { role = Role.Button },
        enabled = enabled, shape = IFShapes.md,
        color = if (enabled && (hovered || pressed)) IFTheme.colors.surfaceSubtle else androidx.compose.ui.graphics.Color.Transparent,
        contentColor = if (enabled) IFTheme.colors.onSurface else IFTheme.colors.secondary,
        border = if (focused || outlined) BorderStroke(if (focused) 2.dp else 1.dp, border) else null,
        interactionSource = interaction,
    ) {
        Box(contentAlignment = Alignment.Center) { content() }
    }
}

@Composable
fun IFOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
    label: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value, onValueChange, modifier, enabled = enabled, readOnly = readOnly,
        interactionSource = interactionSource,
        label = label, singleLine = singleLine, isError = isError,
        textStyle = IFTheme.typography.bodyMd,
        keyboardOptions = keyboardOptions, visualTransformation = visualTransformation,
        trailingIcon = trailingIcon, shape = IFShapes.md,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = IFTheme.colors.primary,
            unfocusedBorderColor = IFTheme.colors.inputOutline,
            focusedContainerColor = IFTheme.colors.surface,
            unfocusedContainerColor = IFTheme.colors.surface,
        ),
    )
}

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
        Box(Modifier.size(IFSize.switchWidth, IFSize.switchHeight).clip(IFShapes.full)
            .background(if (checked && enabled) IFTheme.colors.primary else IFTheme.colors.inputOutline)) {
            Box(Modifier.offset { IntOffset(thumbOffset.roundToPx(), 2.dp.roundToPx()) }.size(20.dp).clip(IFShapes.full)
                .background(if (checked && enabled) IFTheme.colors.onPrimary else IFTheme.colors.surface))
        }
        Text(stringResource(if (checked) R.string.setting_on else R.string.setting_off),
            style = IFTheme.typography.labelMd, color = IFTheme.colors.onSurface)
    }
}

@Composable
fun IFThemeSelector(theme: Theme, onThemeChange: (Theme) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    Box(modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth().heightIn(min = IFSize.themeSelect),
            shape = IFShapes.sm,
            border = BorderStroke(if (focused) 2.dp else 1.dp,
                if (focused) IFTheme.colors.primary else IFTheme.colors.inputOutline),
            interactionSource = interaction,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = IFTheme.colors.surface, contentColor = IFTheme.colors.onSurface),
        ) {
            Text(stringResource(R.string.theme) + ": " + theme.asUiText().asString(),
                Modifier.weight(1f), style = IFTheme.typography.labelMd)
            Icon(IFIcon.Down.vector, null, Modifier.size(IFSize.icon))
        }
        DropdownMenu(expanded, { expanded = false }, containerColor = IFTheme.colors.surface) {
            Theme.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.asUiText().asString()) },
                    onClick = { expanded = false; onThemeChange(option) },
                    leadingIcon = if (option == theme) {{ Icon(IFIcon.Check.vector, null) }} else null,
                    modifier = Modifier.semantics { this.selected = option == theme },
                )
            }
        }
    }
}
