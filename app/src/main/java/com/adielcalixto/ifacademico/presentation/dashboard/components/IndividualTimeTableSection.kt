package com.adielcalixto.ifacademico.presentation.dashboard.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.IndividualTimeTable
import com.adielcalixto.ifacademico.domain.entities.TimeTableClass
import com.adielcalixto.ifacademico.presentation.UiText
import java.text.DateFormatSymbols
import java.time.LocalTime
import java.util.Locale

private fun getLocalizedBusinessDays(): List<String> {
    val locale = Locale.getDefault()
    val dateFormatSymbols = DateFormatSymbols(locale)
    val daysOfWeek = dateFormatSymbols.shortWeekdays
    val businessDays = daysOfWeek.drop(2).dropLast(1)

    return businessDays.map { it.uppercase() }
}

@Composable
internal fun IndividualTimeTableSection(individualTimeTable: IndividualTimeTable) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Schedule(individualTimeTable)
        Spacer(Modifier.height(16.dp))
        CoursesInfos(individualTimeTable.classes.values.flatten().distinctBy { it.className })
    }
}

@Composable
internal fun Schedule(individualTimeTable: IndividualTimeTable) {
    val weekDays = getLocalizedBusinessDays()

    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                weekDays.forEachIndexed { index, day ->
                    val bgColor = if (index + 2 == individualTimeTable.weekDay) MaterialTheme.colorScheme.primaryContainer else Color.Transparent

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(2f)
                            .padding(2.dp)
                            .background(bgColor, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            day,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (index + 2 == individualTimeTable.weekDay) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            for (time in individualTimeTable.times) {
                val formatedTime = LocalTime.parse(time)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(formatedTime.toString(), style = MaterialTheme.typography.labelSmall)
                        Text(formatedTime.plusMinutes(50).toString(), style = MaterialTheme.typography.labelSmall)
                    }

                    for (day in 2..6) {
                        val courses = individualTimeTable.classes[day]?.sortedBy { it.startTime }
                        val course = courses?.find { it.startTime == time }
                        val bgColor = if (course == null) Color.Transparent else MaterialTheme.colorScheme.surface

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.1f)
                                .padding(5.dp)
                                .background(bgColor, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            course.let {
                                Text(
                                    it?.acronym ?: "",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoursesInfos(courses: List<TimeTableClass>) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(UiText.StringResource(R.string.show_course_detail).asString())
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "${courses.size} ${UiText.StringResource(R.string.course).asString()}${if (courses.size > 1) "s" else ""}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    courses.forEach { course ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                "${course.acronym} - ${course.className}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                "${course.classRoomName} - ${course.professorName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (course != courses.last()) {
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}