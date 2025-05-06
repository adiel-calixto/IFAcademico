package com.adielcalixto.ifacademico.presentation.diary_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.Period
import com.adielcalixto.ifacademico.presentation.UiText

private fun Period.asString() = "%02d/%d".format(number, year)

@Composable
fun PeriodsDropdown(
    periods: List<Period>,
    selectedPeriod: Period?,
    onPeriodSelected: (p: Period) -> Unit
) {
    var dropDownExpanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { dropDownExpanded = !dropDownExpanded }) {
        Text(
            UiText.StringResource(R.string.period).asString() + ":",
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Box {
            Row {
                Text(selectedPeriod?.asString() ?: "")
                Spacer(modifier = Modifier.padding(2.dp))
                Icon(Icons.Filled.ArrowDropDown, "Arrow down")
            }
            DropdownMenu(
                modifier = Modifier.padding(4.dp),
                expanded = dropDownExpanded,
                onDismissRequest = {
                    dropDownExpanded = false
                }) {
                periods.forEach { period ->
                    DropdownMenuItem(
                        onClick = {
                            dropDownExpanded = false
                            onPeriodSelected(period)
                        }) {
                        Box {
                            Text(period.asString())
                        }
                    }
                }
            }
        }
    }
}