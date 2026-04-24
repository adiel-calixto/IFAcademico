package com.adielcalixto.ifacademico.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.IndividualTimeTable
import com.adielcalixto.ifacademico.domain.entities.TimeTableClass
import com.adielcalixto.ifacademico.presentation.UiText
import java.text.DateFormatSymbols
import java.time.LocalTime
import java.util.Locale

private val PREPOSITIONS = setOf(
    "DE", "DA", "DO", "DAS", "DOS",
    "EM", "NA", "NO", "NAS", "NOS",
    "A", "O", "AS", "OS",
    "E", "OU",
    "PARA", "POR", "COM", "SEM", "À"
)

private val ROMAN_NUMERAL_REGEX = Regex("^(X{0,3})(IX|IV|V?I{0,3})$")

private fun generateAcronym(className: String, maxLength: Int = 3): AnnotatedString {
    val words = extractValidWords(className)
    val courseNumber = extractCourseNumber(words)

    if (words.isEmpty()) return AnnotatedString.Builder().toAnnotatedString()

    val wordsWithoutCourseNumber = if (courseNumber.isNotEmpty()) words.dropLast(1) else words
    val chars = mutableListOf<Char>()

    chars.addAll(
        wordsWithoutCourseNumber.take(maxLength)
            .map { it.first().uppercaseChar() }
    )

    var charIndex = 1
    while (chars.size < maxLength && charIndex < wordsWithoutCourseNumber.maxOf { it.length }) {
        for ((wordIndex, word) in wordsWithoutCourseNumber.withIndex()) {
            if (chars.size >= maxLength) break

            if (charIndex < word.length) {
                val insertPosition = wordIndex * wordsWithoutCourseNumber.size + charIndex
                chars.add(
                    insertPosition.coerceAtMost(chars.size),
                    word[charIndex].uppercaseChar()
                )
            }
        }
        charIndex++
    }

    return buildAnnotatedAcronym(chars.take(maxLength), courseNumber)
}

private fun extractValidWords(className: String): List<String> {
    return className.split("\\s+".toRegex())
        .map { it.filter(Char::isLetter) }
        .filter { it.isNotEmpty() && it !in PREPOSITIONS }
}

private fun extractCourseNumber(words: List<String>): String {
    if (words.isEmpty()) return ""

    return words.last()
        .takeIf { it.matches(ROMAN_NUMERAL_REGEX) }
        .orEmpty()
}

private fun buildAnnotatedAcronym(acronymChars: List<Char>, courseNumber: String): AnnotatedString {
    val fullText = acronymChars.joinToString("") + courseNumber

    return AnnotatedString.Builder(fullText).apply {
        if (courseNumber.isNotEmpty()) {
            addStyle(
                style = SpanStyle(fontFamily = FontFamily.Serif),
                start = acronymChars.size,
                end = fullText.length
            )
        }
    }.toAnnotatedString()
}

private fun getLocalizedBusinessDays(): List<String> {
    val locale = Locale.getDefault()
    val dateFormatSymbols = DateFormatSymbols(locale)
    val daysOfWeek = dateFormatSymbols.shortWeekdays
    val businessDays = daysOfWeek.drop(2).dropLast(1)

    return businessDays.map { it.uppercase() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun IndividualTimeTableSection(
    individualTimeTable: IndividualTimeTable,
    onReturnClicked: () -> Unit
) {
    var selectedCourse by remember { mutableStateOf<TimeTableClass?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            tonalElevation = 1.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                NavigationBar(onReturnClicked = onReturnClicked)
                Spacer(modifier = Modifier.height(16.dp))
                Schedule(
                    individualTimeTable = individualTimeTable,
                    onCourseClicked = { selectedCourse = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = UiText.StringResource(R.string.tap_to_see_details).asString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    selectedCourse?.let { course ->
        CourseBottomSheet(
            course = course,
            sheetState = sheetState,
            onDismiss = { selectedCourse = null }
        )
    }
}

@Composable
internal fun NavigationBar(onReturnClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onReturnClicked) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Return button"
            )
        }
        Text(
            text = UiText.StringResource(R.string.weekly_schedule).asString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
internal fun Schedule(
    individualTimeTable: IndividualTimeTable,
    onCourseClicked: (TimeTableClass) -> Unit
) {
    val weekDays = getLocalizedBusinessDays()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            weekDays.forEachIndexed { index, day ->
                val bgColor =
                    if (index + 2 == individualTimeTable.weekDay) MaterialTheme.colorScheme.primaryContainer else Color.Transparent

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
                    Text(
                        formatedTime.plusMinutes(50).toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                for (day in 2..6) {
                    val courses = individualTimeTable.classes[day]?.sortedBy { it.startTime }
                    val course = courses?.find { it.startTime == time }
                    val bgColor =
                        if (course == null) Color.Transparent else MaterialTheme.colorScheme.surface

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.1f)
                            .padding(5.dp)
                            .background(bgColor, RoundedCornerShape(10.dp))
                            .then(
                                if (course != null) Modifier.clickable { onCourseClicked(course) }
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (course != null) {
                            Text(
                                generateAcronym(course.className),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseBottomSheet(
    course: TimeTableClass,
    sheetState: androidx.compose.material3.SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = course.className,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${course.startTime} - ${course.endTime}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Room,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = course.classRoomName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = course.professorName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
