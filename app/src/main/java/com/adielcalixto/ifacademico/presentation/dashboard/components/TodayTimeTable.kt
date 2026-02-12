package com.adielcalixto.ifacademico.presentation.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.TimeTable
import com.adielcalixto.ifacademico.presentation.UiText

@Composable
internal fun TodayTimeTable(timeTable: TimeTable) {
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                "${
                    UiText.StringResource(R.string.classes_of_the_day).asString()
                } ${timeTable.today}",
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(8.dp))

            if (timeTable.classes.isEmpty()) {
                Text(UiText.StringResource(R.string.empty_timetable_classes).asString())
            } else {
                Column {
                    timeTable.classes.forEach { timeTableClass ->
                        Column(modifier = Modifier.padding(vertical = 16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⚬")
                                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                Column {
                                    Text(
                                        timeTableClass.className,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        timeTableClass.professorName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Light
                                    )
                                }
                            }

                            HorizontalDivider(
                                thickness = 1.dp,
                                modifier = Modifier.padding(8.dp)
                            )

                            Row {
                                Text(timeTableClass.classRoomName)
                                Spacer(modifier = Modifier.weight(1f))
                                Text("${timeTableClass.startTime} - ${timeTableClass.endTime}")
                            }
                        }
                    }
                }
            }
        }
    }
}