package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.presentation.UiText
import com.adielcalixto.ifacademico.presentation.asUiText

@Composable
fun ErrorComponent(error: Error.DataError, onRetryClicked: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 64.dp)
    ) {
        Icon(Icons.Filled.ErrorOutline, UiText.StringResource(R.string.error).asString(), modifier = Modifier.padding(16.dp))

        Text(UiText.StringResource(R.string.error).asString(), style = MaterialTheme.typography.titleMedium)
        Text(error.asUiText().asString(), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.padding(16.dp))

        Button(
            onClick = onRetryClicked
        ) {
            Text(UiText.StringResource(R.string.retry).asString())
        }
    }
}