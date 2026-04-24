package com.adielcalixto.ifacademico.presentation.diary_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        DualProgressBar(
            maxValue = plannedClasses,
            firstProgress = taughtClasses,
            secondProgress = pendingClasses,
            firstLabel = UiText.StringResource(R.string.taught_classes).asString(),
            secondLabel = UiText.StringResource(R.string.pending_classes).asString()
        )
    }
}