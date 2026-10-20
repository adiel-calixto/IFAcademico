package com.adielcalixto.ifacademico.presentation.student_info

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.adielcalixto.ifacademico.domain.entities.Student
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.Theme
import com.adielcalixto.ifacademico.presentation.components.*

@IFCatalogPreviews
@Composable
internal fun StudentInfoPreview() {
    var theme by remember { mutableStateOf(Theme.SYSTEM) }
    var updates by remember { mutableStateOf(true) }
    IFTheme {
        Scaffold(
            topBar = { IFHeader({}) },
            bottomBar = {
                IFBottomNavigation(
                    listOf(
                        IFNavigationItem("Hoje", IFIcon.Home),
                        IFNavigationItem("Diários", IFIcon.Grades),
                        IFNavigationItem("Calendário", IFIcon.Calendar),
                    ), -1, {},
                )
            },
        ) { padding ->
            StudentInfoContent(
                Student("Ana Maria de Oliveira", 42, 1, "Bacharelado em Ciência da Computação"),
                theme, { theme = it }, updates, { updates = it }, {}, {},
                Modifier.padding(padding),
            )
        }
    }
}
