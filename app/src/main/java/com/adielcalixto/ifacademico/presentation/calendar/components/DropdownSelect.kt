package com.adielcalixto.ifacademico.presentation.calendar.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private fun LocalDate.asString(): String {
    return this.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        .replaceFirstChar { it.uppercase() }
}

@Composable
fun DropdownSelect(
    options: List<LocalDate>,
    label: String,
    onSelect: (selected: LocalDate) -> Unit,
    initialIndex: Int = 0
) {
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(initialIndex) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)

    Column {
        OutlinedTextField(
            value = options[selectedIndex].asString(),
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            label = { Text(label) },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                expanded = !expanded
                            }
                        }
                    }
                },
            trailingIcon = {
                Icon(
                    Icons.Filled.KeyboardArrowDown, "Arrow",
                    Modifier
                        .rotate(rotation)
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selectedIndex = options.indexOf(option)
                        expanded = false
                        onSelect(option)
                    },
                    text = { Text(option.asString()) }
                )
            }
        }
    }
}