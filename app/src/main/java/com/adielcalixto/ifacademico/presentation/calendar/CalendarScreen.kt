package com.adielcalixto.ifacademico.presentation.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.asUiText
import com.adielcalixto.ifacademico.presentation.calendar.components.IFEventRow
import com.adielcalixto.ifacademico.presentation.calendar.components.EventsLoading
import com.adielcalixto.ifacademico.presentation.calendar.components.IFMonthGrid
import com.adielcalixto.ifacademico.presentation.components.*
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val state by viewModel.state.collectAsState()
    CalendarContent(state, viewModel::onAction)
}

@Composable
internal fun CalendarContent(
    state: CalendarState,
    onAction: (CalendarAction) -> Unit,
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now(),
) {
    val locale = LocalConfiguration.current.locales[0]
    val month = YearMonth.from(state.selectedDate)
    val listState = rememberLazyListState()
    val monthKey = month.toString()
    var displayedMonth by rememberSaveable { mutableStateOf(monthKey) }
    LaunchedEffect(monthKey) {
        if (displayedMonth != monthKey) {
            listState.scrollToItem(0)
            displayedMonth = monthKey
        }
    }
    val dates =
        remember(state.specialDates, month) { specialDatesForMonth(state.specialDates, month) }
    val colors = IFTheme.colors
    val dayMarkers = remember(dates, month, colors, state.isLoading, state.error) {
        if (state.isLoading || state.error != null) emptyMap() else calendarDayMarkers(
            dates,
            month,
            colors
        )
    }

    LazyColumn(
        modifier.fillMaxSize(), state = listState,
        contentPadding = PaddingValues(horizontal = IFTheme.pageInset, vertical = IFSpacing.lg),
    ) {
        item(key = "month") {
            IFMonthGrid(
                month, today, dayMarkers,
                onPrevious = {
                    onAction(
                        CalendarAction.OnMonthChanged(
                            month.minusMonths(1).atDay(1)
                        )
                    )
                },
                onNext = { onAction(CalendarAction.OnMonthChanged(month.plusMonths(1).atDay(1))) },
            )
            Spacer(Modifier.height(IFSpacing.lg))
        }
        item(key = "heading") {
            FlowRow(
                Modifier.fillMaxWidth().padding(bottom = IFSpacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(IFSpacing.xs),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                IFSectionTitle(stringResource(R.string.special_dates))
                if (!state.isLoading && state.error == null) {
                    Text(
                        pluralStringResource(R.plurals.special_date_count, dates.size, dates.size),
                        style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary
                    )
                }
            }
        }
        when {
            state.isLoading -> item { EventsLoading() }
            state.error != null -> item {
                IFErrorState(state.error.asUiText().asString(), { onAction(CalendarAction.Retry) })
            }

            dates.isEmpty() -> item { IFEmptyState(stringResource(R.string.no_special_dates)) }
            else -> items(dates) { date ->
                IFEventRow(
                    description = date.description,
                    date = date.formattedCompactDateRange(locale),
                    dateDescription = date.formattedDateRange(locale),
                )
            }
        }
    }
}
