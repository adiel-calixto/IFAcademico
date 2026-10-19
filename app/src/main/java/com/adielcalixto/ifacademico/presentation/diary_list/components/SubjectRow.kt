package com.adielcalixto.ifacademico.presentation.diary_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.IFIcon

data class IFSubjectRecord(val name: String, val teacher: String, val assessmentCount: Int?, val hours: Int)

@Composable
fun IFSubjectRow(subject: IFSubjectRecord, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = IFSpacing.md), horizontalArrangement = Arrangement.spacedBy(IFSpacing.sm)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
                Text(subject.name, style = IFTheme.typography.detailTitle, color = IFTheme.colors.onSurface)
                Text(subject.teacher, style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
                Text(subject.assessmentCount?.let {
                    pluralStringResource(R.plurals.subject_assessments, it, it, subject.hours)
                } ?: stringResource(R.string.hours_value, subject.hours),
                    style = IFTheme.typography.labelSm, color = IFTheme.colors.primary)
            }
            Icon(IFIcon.Chevron.vector, null, Modifier.size(IFSize.icon), tint = IFTheme.colors.secondary)
        }
        HorizontalDivider(color = IFTheme.colors.outline)
    }
}
