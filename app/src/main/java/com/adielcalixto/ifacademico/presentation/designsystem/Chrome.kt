package com.adielcalixto.ifacademico.presentation.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun IFWordmark(modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
        Box(Modifier.size(IFSize.mark).clip(IFShapes.brand).background(IFTheme.colors.primary),
            contentAlignment = Alignment.Center) {
            Text("IF", color = IFTheme.colors.onPrimary, style = IFTheme.typography.labelSm)
        }
        Text("IFAcademico", style = IFTheme.typography.wordmark, color = IFTheme.colors.onSurface)
    }
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

@Composable
fun IFInitialsAvatar(name: String, modifier: Modifier = Modifier) {
    val initials = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.take(2)
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
fun IFRecord(label: String, value: String, modifier: Modifier = Modifier) {
    FlowRow(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
        Text(label, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
        Text(value, style = IFTheme.typography.bodyMd, color = IFTheme.colors.onSurface)
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

@Composable
fun IFSettingsRow(label: String, helper: String?, modifier: Modifier = Modifier, control: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
        Text(label, style = IFTheme.typography.labelMd, color = IFTheme.colors.onSurface)
        helper?.let { Text(it, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary) }
        control()
    }
}
