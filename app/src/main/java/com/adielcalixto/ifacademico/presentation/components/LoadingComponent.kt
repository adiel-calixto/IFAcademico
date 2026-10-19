package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adielcalixto.ifacademico.presentation.IFTheme

@Composable
fun LoadingComponent() {
    IFLoadingPlaceholder(Modifier.fillMaxWidth().padding(IFTheme.pageInset))
}
