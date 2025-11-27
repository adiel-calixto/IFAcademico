package com.adielcalixto.ifacademico.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.SpecialDate
import com.adielcalixto.ifacademico.presentation.UiText
import com.adielcalixto.ifacademico.presentation.asUiText
import com.adielcalixto.ifacademico.presentation.calendar.components.DropdownSelect
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.util.Locale

private fun getLocalizedDaysOfWeek(): List<String> {
    val locale = Locale.getDefault()
    val dateFormatSymbols = DateFormatSymbols(locale)
    val daysOfWeek = dateFormatSymbols.weekdays

    return daysOfWeek.drop(1).map { it.substring(0..2).uppercase() }
}

private fun parseColor(color: String): Color {
    val colorInt = android.graphics.Color.parseColor(color)

    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(colorInt, hsv)

    // Reduce color saturation
    hsv[1] *= 0.25f
    hsv[1] = hsv[1].coerceIn(0f, 1f)

    hsv[2] += 0.4f
    hsv[2] = hsv[2].coerceIn(0f, 1f)

    return Color(android.graphics.Color.HSVToColor(hsv))
}

private fun computeSpecialDatesMap(specialDates: List<SpecialDate>): Map<Int, SpecialDate> {
    val sdMap = HashMap<Int, SpecialDate>()

    specialDates.forEach { specialDate ->
        val startDay = specialDate.startDate.dayOfMonth
        val endDay = if (specialDate.startDate.month <= specialDate.endDate.month) {
            specialDate.endDate.dayOfMonth
        } else {
            31
        }

        (startDay..endDay).forEach { day ->
            sdMap[day] = specialDate
        }
    }

    return sdMap
}

@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val state by viewModel.state.collectAsState()
    val monthOptions = viewModel.getMonthOptions()

    Column(
        modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DropdownSelect(
            monthOptions,
            label = UiText.StringResource(R.string.month).asString(),
            { viewModel.onAction(CalendarAction.OnMonthChanged(it)) },
            monthOptions.indexOf(state.selectedDate)
        )

        Spacer(modifier = Modifier.padding(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .width(50.dp)
            )
            return@Column
        }

        if (state.error != null) {
            Text(state.error!!.asUiText().asString())
            return@Column
        }

        // Calendar grid
        Surface(
            tonalElevation = 1.dp, shape = RoundedCornerShape(12.dp)
        ) {
            val firstDayOfMonth = state.selectedDate.withDayOfMonth(1)
            val daysInMonth = state.selectedDate.lengthOfMonth()
            val firstWeekday = firstDayOfMonth.dayOfWeek.value % 7

            val weekDays = getLocalizedDaysOfWeek()

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weekDays.forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Column {
                    var dayIndex = 0
                    val specialDatesMap = computeSpecialDatesMap(state.specialDates)

                    for (week in 0..5) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (weekday in 0..6) {
                                if (week == 0 && weekday < firstWeekday || dayIndex >= daysInMonth) {
                                    Spacer(modifier = Modifier.weight(1f))
                                } else {
                                    val day = dayIndex + 1

                                    val specialDate = specialDatesMap[day]
                                    val bgColor = specialDate?.color?.let { parseColor(it) }
                                        ?: Color.Transparent

                                    Column(
                                        modifier = Modifier
                                            .padding(2.dp)
                                            .weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .aspectRatio(1f)
                                                .background(bgColor, RoundedCornerShape(12.dp))
                                                .padding(2.dp), contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                day.toString(),
                                                color = if (bgColor == Color.Transparent) MaterialTheme.colorScheme.onSurface else Color.DarkGray
                                            )
                                        }
                                    }
                                    dayIndex++
                                }
                            }
                        }
                    }
                }

                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(state.specialDates) { specialDate ->
                        Row(modifier = Modifier.padding(1.dp)) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(.3f)
                            ) {
                                if (specialDate.startDate.toLocalDate() == specialDate.endDate.toLocalDate()) {
                                    Text(
                                        specialDate.startDate.dayOfMonth.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        "${specialDate.startDate.dayOfMonth} ~ ${specialDate.endDate.dayOfMonth}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.padding(8.dp))

                            Text(
                                specialDate.description,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}