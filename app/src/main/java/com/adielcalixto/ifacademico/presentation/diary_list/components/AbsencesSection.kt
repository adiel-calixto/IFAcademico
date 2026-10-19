package com.adielcalixto.ifacademico.presentation.diary_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.UiText
import com.adielcalixto.ifacademico.presentation.designsystem.IFShapes

@Composable
internal fun AbsencesSection(absences: Int, maxAbsences: Int, excusedAbsences: Int) {
    Row (modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Surface (tonalElevation = 0.dp, modifier = Modifier.weight(1f).fillMaxHeight(), shape = IFShapes.md) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(absences.toString(), style = MaterialTheme.typography.titleMedium)
                Text(UiText.StringResource(R.string.absences).asString(), style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.padding(16.dp))

        Surface (tonalElevation = 0.dp, modifier = Modifier.weight(1f), shape = IFShapes.md) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(maxAbsences.toString(), style = MaterialTheme.typography.titleMedium)
                Text(UiText.StringResource(R.string.max_absences).asString(), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}