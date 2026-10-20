package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme

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
