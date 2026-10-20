package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.asUiText

/** Whole-page recovery, centered in the content viewport and scrollable at large text sizes. */
@Composable
fun ErrorComponent(error: Error.DataError, onRetryClicked: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min = maxHeight)
                .padding(IFTheme.pageInset),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IFErrorState(
                error.asUiText().asString(),
                onRetryClicked,
                Modifier.widthIn(max = 480.dp)
            )
        }
    }
}
