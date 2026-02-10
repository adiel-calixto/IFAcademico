package com.adielcalixto.ifacademico.presentation.diary_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.UiText

@Composable
fun ClassesSummary(
    modifier: Modifier = Modifier,
    plannedClasses: Int,
    taughtClasses: Int,
    pendingClasses: Int
) {
    Column(modifier = modifier) {
        Text(
            UiText.StringResource(R.string.classes_summary).asString(),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        DualProgressBar(
            maxValue = plannedClasses,
            firstProgress = taughtClasses,
            secondProgress = pendingClasses,
            firstLabel = UiText.StringResource(R.string.taught_classes).asString(),
            secondLabel = UiText.StringResource(R.string.pending_classes).asString()
        )
    }
}