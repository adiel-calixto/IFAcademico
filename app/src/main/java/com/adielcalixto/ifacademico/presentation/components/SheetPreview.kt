package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme

@IFCatalogPreviews
@Composable
internal fun SheetPreview(maxWidth: Dp = Dp.Unspecified) {
    IFTheme {
        var opened by remember { mutableStateOf(true) }
        val opener = remember { FocusRequester() }
        Surface(color = IFTheme.colors.background) {
            IFButton({ opened = true }, Modifier.focusRequester(opener).padding(24.dp)) { Text("Open details") }
        }
        if (opened) IFSlidingSheet("Subject details", { opened = false },
            modifier = if (maxWidth == Dp.Unspecified) Modifier else Modifier.widthIn(max = maxWidth),
            returnFocus = opener, onBack = {}) {
            repeat(12) { index ->
                IFRecord("Record ${index + 1}", "Read-only academic detail")
                Spacer(Modifier.height(IFSpacing.lg))
            }
        }
    }
}
