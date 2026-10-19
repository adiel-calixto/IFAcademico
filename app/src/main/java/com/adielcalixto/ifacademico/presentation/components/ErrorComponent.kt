package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.asUiText

@Composable
fun ErrorComponent(error: Error.DataError, onRetryClicked: () -> Unit) {
    IFErrorState(error.asUiText().asString(), onRetryClicked,
        Modifier.fillMaxWidth().padding(IFTheme.pageInset))
}
