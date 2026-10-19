package com.adielcalixto.ifacademico.presentation.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

/** The caller supplies every marked date, including each day of multi-day events. */
@Composable
fun IFMonthGrid(
    month: YearMonth,
    today: LocalDate?,
    eventDays: Set<LocalDate>,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
) {
    val locale = Locale.getDefault()
    val heading = month.format(DateTimeFormatter.ofPattern("LLLL yyyy", locale))
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(IFSpacing.sm)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(heading, Modifier.weight(1f), style = IFTheme.typography.titleSm,
                color = IFTheme.colors.onSurface)
            IFIconButton(onPrevious) {
                Icon(IFIcon.Back.vector, stringResource(R.string.previous_month), Modifier.size(IFSize.icon))
            }
            IFIconButton(onNext) {
                Icon(IFIcon.Chevron.vector, stringResource(R.string.next_month), Modifier.size(IFSize.icon))
            }
        }
        Row(Modifier.fillMaxWidth()) {
            repeat(7) { index ->
                val day = firstDayOfWeek.plus(index.toLong())
                Text(day.getDisplayName(TextStyle.NARROW, locale), Modifier.weight(1f),
                    style = IFTheme.typography.labelSm, color = IFTheme.colors.secondary, textAlign = TextAlign.Center)
            }
        }
        val offset = (month.atDay(1).dayOfWeek.value - firstDayOfWeek.value + 7) % 7
        val weeks = (offset + month.lengthOfMonth() + 6) / 7
        repeat(weeks) { week ->
            Row(Modifier.fillMaxWidth()) {
                repeat(7) { weekday ->
                    val day = week * 7 + weekday - offset + 1
                    if (day !in 1..month.lengthOfMonth()) {
                        Spacer(Modifier.weight(1f))
                    } else {
                        val date = month.atDay(day)
                        val event = date in eventDays
                        val isToday = date == today
                        val status = listOfNotNull(
                            date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)),
                            if (isToday) stringResource(R.string.today_label) else null,
                            if (event) stringResource(R.string.event_or_assessment) else null,
                        ).joinToString(", ")
                        Column(
                            Modifier.weight(1f).padding(2.dp)
                                .clip(IFShapes.sm)
                                .background(if (event) IFTheme.colors.primaryContainer else IFTheme.colors.background)
                                .then(if (isToday) Modifier.border(1.dp, IFTheme.colors.primary, IFShapes.sm) else Modifier)
                                .clearAndSetSemantics { contentDescription = status }
                                .heightIn(min = 44.dp).padding(vertical = IFSpacing.micro),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(day.toString(), style = IFTheme.typography.bodySm, color = IFTheme.colors.onSurface)
                            if (event) {
                                Box(Modifier.width(16.dp).height(1.dp).background(IFTheme.colors.primary))
                                Spacer(Modifier.height(2.dp))
                                Box(Modifier.size(3.dp).clip(CircleShape).background(IFTheme.colors.primary))
                            }
                        }
                    }
                }
            }
        }
    }
}
