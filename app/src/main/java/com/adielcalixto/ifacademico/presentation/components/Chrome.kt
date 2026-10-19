package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme

@Composable
fun IFWordmark(modifier: Modifier = Modifier) {
    Text(
        "IFAcademico",
        modifier = modifier,
        style = IFTheme.typography.wordmark,
        color = IFTheme.colors.onSurface
    )
}

@Composable
fun IFHeader(
    onProfile: () -> Unit,
    modifier: Modifier = Modifier,
    periodControl: (@Composable () -> Unit)? = null,
) {
    Surface(modifier, color = IFTheme.colors.surface) {
        FlowRow(
            Modifier.fillMaxWidth().padding(horizontal = IFTheme.pageInset, vertical = IFSpacing.xs),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(IFSpacing.xs),
        ) {
            IFWordmark(Modifier.align(Alignment.CenterVertically))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
                periodControl?.invoke()
                IFIconButton(onProfile, outlined = true) {
                    Icon(IFIcon.Profile.vector, stringResource(R.string.open_profile), Modifier.size(IFSize.icon))
                }
            }
        }
    }
}

data class IFNavigationItem(val label: String, val icon: IFIcon)

@Composable
fun IFBottomNavigation(
    items: List<IFNavigationItem>, selectedIndex: Int, onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier, color = IFTheme.colors.surface) {
        Row(Modifier.fillMaxWidth().selectableGroup().padding(horizontal = IFSpacing.xs, vertical = IFSpacing.xs),
            horizontalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
            items.forEachIndexed { index, item ->
                val selected = selectedIndex == index
                val interaction = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                val focused = interaction.collectIsFocusedAsState().value
                Column(
                    Modifier.weight(1f).clip(IFShapes.md)
                        .background(if (selected) IFTheme.colors.primaryContainer else IFTheme.colors.surface)
                        .then(if (focused) Modifier.border(2.dp, IFTheme.colors.primary, IFShapes.md) else Modifier)
                        .selectable(selected, role = Role.Tab, interactionSource = interaction,
                            indication = ripple(), onClick = { onSelect(index) })
                        .heightIn(min = IFSize.touchTarget).padding(IFSpacing.xs),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(IFSpacing.micro),
                ) {
                    val color = if (selected) IFTheme.colors.primary else IFTheme.colors.secondary
                    Icon(item.icon.vector, null, Modifier.size(IFSize.icon), tint = color)
                    Text(item.label, style = IFTheme.typography.labelSm, color = color, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
