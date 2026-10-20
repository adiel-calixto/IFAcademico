package com.adielcalixto.ifacademico.presentation.calendar.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFMotion
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.IFIcon
import com.adielcalixto.ifacademico.presentation.components.IFIconButton
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle

@Immutable
data class IFCalendarDayMarker(
    val background: Color,
    val foreground: Color,
    val eventTypes: List<String>,
)

/** The caller supplies display colors and event types for every day of multi-day events. */
@Composable
fun IFMonthGrid(
    month: YearMonth,
    today: LocalDate?,
    markers: Map<LocalDate, IFCalendarDayMarker>,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
) {
    val locale = LocalConfiguration.current.locales[0]
    val monthHeading = month.format(DateTimeFormatter.ofPattern("LLLL yyyy", locale))
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(IFSpacing.sm)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                monthHeading,
                Modifier.weight(1f).semantics { heading(); liveRegion = LiveRegionMode.Polite },
                style = IFTheme.typography.titleSm,
                color = IFTheme.colors.onSurface
            )
            IFIconButton(onPrevious) {
                Icon(
                    IFIcon.Back.vector,
                    stringResource(R.string.previous_month),
                    Modifier.size(IFSize.icon)
                )
            }
            IFIconButton(onNext) {
                Icon(
                    IFIcon.Chevron.vector,
                    stringResource(R.string.next_month),
                    Modifier.size(IFSize.icon)
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            repeat(7) { index ->
                val day = firstDayOfWeek.plus(index.toLong())
                Text(
                    day.getDisplayName(TextStyle.NARROW, locale),
                    Modifier.weight(1f).clearAndSetSemantics {},
                    style = IFTheme.typography.labelSm,
                    color = IFTheme.colors.secondary,
                    textAlign = TextAlign.Center
                )
            }
        }
        val travel = with(LocalDensity.current) { IFMotion.pageTravel.roundToPx() }
        AnimatedContent(
            targetState = MonthGridSnapshot(month, today, markers, firstDayOfWeek),
            contentKey = { it.month },
            transitionSpec = {
                val direction = if (targetState.month > initialState.month) 1 else -1
                ((fadeIn(tween(IFMotion.calendarMonthMillis)) +
                    slideInHorizontally(tween(IFMotion.calendarMonthMillis)) { direction * travel })
                    togetherWith (fadeOut(tween(IFMotion.calendarMonthMillis)) +
                    slideOutHorizontally(tween(IFMotion.calendarMonthMillis)) { -direction * travel }))
                    .using(SizeTransform { _, _ -> tween(IFMotion.calendarMonthMillis) })
            },
            label = "calendar month",
        ) { snapshot ->
            MonthDates(
                snapshot,
                // Only the current month belongs in the accessibility tree during a transition.
                if (snapshot.month == month) Modifier else Modifier.clearAndSetSemantics {},
            )
        }
    }
}

private data class MonthGridSnapshot(
    val month: YearMonth,
    val today: LocalDate?,
    val markers: Map<LocalDate, IFCalendarDayMarker>,
    val firstDayOfWeek: DayOfWeek,
)

@Composable
private fun MonthDates(snapshot: MonthGridSnapshot, modifier: Modifier = Modifier) {
    val (month, today, markers, firstDayOfWeek) = snapshot
    val locale = LocalConfiguration.current.locales[0]
    val offset = (month.atDay(1).dayOfWeek.value - firstDayOfWeek.value + 7) % 7
    val weeks = (offset + month.lengthOfMonth() + 6) / 7
    Column(modifier) {
        repeat(weeks) { week ->
            Row(Modifier.fillMaxWidth()) {
                repeat(7) { weekday ->
                    val day = week * 7 + weekday - offset + 1
                    if (day !in 1..month.lengthOfMonth()) {
                        Spacer(Modifier.weight(1f))
                    } else {
                        val date = month.atDay(day)
                        val marker = markers[date]
                        val isToday = date == today
                        val status = (listOfNotNull(
                            date.format(
                                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                                    .withLocale(locale)
                            ),
                            if (isToday) stringResource(R.string.today_label) else null,
                            if (marker != null) stringResource(R.string.special_date_marker) else null,
                        ) + marker?.eventTypes.orEmpty()).joinToString(", ")
                        Column(
                            Modifier.weight(1f).padding(2.dp)
                                .aspectRatio(1f)
                                .clip(IFShapes.sm)
                                .background(marker?.background ?: IFTheme.colors.background)
                                .then(
                                    if (isToday) Modifier.border(
                                        1.dp,
                                        IFTheme.colors.primary,
                                        IFShapes.sm
                                    ) else Modifier
                                )
                                .clearAndSetSemantics { contentDescription = status },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            // Single-line numerals need room to grow within square cells.
                            Text(
                                day.toString(),
                                style = IFTheme.typography.bodySm.copy(
                                    lineHeight = IFTheme.typography.bodySm.fontSize,
                                ),
                                color = marker?.foreground ?: IFTheme.colors.onSurface,
                            )
                            if (marker != null) {
                                Box(
                                    Modifier.width(16.dp).height(1.dp)
                                        .background(marker.foreground)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
