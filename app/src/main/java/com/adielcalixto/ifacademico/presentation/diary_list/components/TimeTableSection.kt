package com.adielcalixto.ifacademico.presentation.diary_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.domain.entities.TimeTableClass
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

private fun createAcronym(courseName: String): String {
    val eachWord: List<String> = courseName.split(" ", "-")
    val saveCourseNumber = if(listOf("I", "II", "III", "IV", "V").indexOf(eachWord.last()) != -1) eachWord.last() else ""

    val removePreposition = eachWord.filter { s ->  s.count() > 3}
    val justFirstLetter: List<Char> = removePreposition.map { it.get(0) }

    return justFirstLetter.joinToString("") { it.uppercase() } + saveCourseNumber
}

@Composable
internal fun TimeTableSection(coursesByDay: Map<String, Map<Int, TimeTableClass>>, todayWeekDay: Int) {
    val weekDays = getLocalizedBusinessDays()
    val orderedTimes = coursesByDay.keys.sorted()

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
                    val bgColor = if (index + 2 == todayWeekDay) MaterialTheme.colorScheme.primaryContainer else Color.Transparent

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
                            color = if (index + 2 == todayWeekDay) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            for (time in orderedTimes) {
                val formatedTime = LocalTime.parse(time)
                val coursesMap = coursesByDay[time]

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
                        val course = coursesMap?.get(day)
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
                                    createAcronym(it?.className ?: ""),
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
