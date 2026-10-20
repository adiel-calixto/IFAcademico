package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.adielcalixto.ifacademico.presentation.IFSpacing

@IFCatalogPreviews
@Composable
internal fun ControlsPreview() = Catalog {
    var text by remember { mutableStateOf("") }
    IFSectionTitle("Controls")
    IFButton({}) { Text("Continue") }
    IFButton({}, enabled = false) { Text("Disabled") }
    IFButton({}, destructive = true) { Text("Log out") }
    IFTextButton({}) { Text("Secondary action") }
    IFOutlinedTextField(text, { text = it }, Modifier.fillMaxWidth(), label = { Text("Enrollment ID") })
    IFOutlinedTextField("Invalid input", {}, Modifier.fillMaxWidth(), label = { Text("Error") }, isError = true)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
        IFIcon.entries.forEach { icon -> IFIconButton({}, outlined = true) { Icon(icon.vector, icon.name) } }
    }
}
