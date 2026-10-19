package com.adielcalixto.ifacademico.presentation.student_info.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.adielcalixto.ifacademico.presentation.Theme
import com.adielcalixto.ifacademico.presentation.components.Catalog
import com.adielcalixto.ifacademico.presentation.components.IFBottomNavigation
import com.adielcalixto.ifacademico.presentation.components.IFCatalogPreviews
import com.adielcalixto.ifacademico.presentation.components.IFHeader
import com.adielcalixto.ifacademico.presentation.components.IFIcon
import com.adielcalixto.ifacademico.presentation.components.IFNavigationItem
import com.adielcalixto.ifacademico.presentation.components.IFNotice
import com.adielcalixto.ifacademico.presentation.components.IFRecord
import com.adielcalixto.ifacademico.presentation.components.IFTextButton
import com.adielcalixto.ifacademico.presentation.components.IFWordmark

@IFCatalogPreviews
@Composable
internal fun ChromePreview() = Catalog {
    var selected by remember { mutableIntStateOf(0) }
    IFWordmark()
    IFHeader({})
    IFHeader({}, periodControl = { IFTextButton({}) { Text("2026 · 2") } })
    IFBottomNavigation(
        listOf(IFNavigationItem("Today", IFIcon.Home), IFNavigationItem("Grades", IFIcon.Grades),
            IFNavigationItem("Calendar", IFIcon.Calendar)), selected, { selected = it },
    )
    IFProfileIdentity("Ana Maria de Oliveira", "Computer Science and Information Systems")
    IFRecord("Enrollment ID", "202610012345")
    IFRecord("Account type", "Student")
    IFNotice("Your account requires a password change. Use your institution’s account service.")
    IFSettingsRow("Theme", "Choose an appearance or follow your device.") { IFThemeSelector(Theme.SYSTEM, {}) }
    IFSettingsRow("Check for updates", "Checks for new versions. You choose when to install updates.") {
        IFUpdateSwitch(true, {})
    }
}
