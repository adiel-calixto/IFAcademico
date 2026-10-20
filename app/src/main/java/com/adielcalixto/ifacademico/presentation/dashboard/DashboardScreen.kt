package com.adielcalixto.ifacademico.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.TimeTableClass
import com.adielcalixto.ifacademico.presentation.asUiText
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.ErrorComponent
import com.adielcalixto.ifacademico.presentation.components.IFEmptyState
import com.adielcalixto.ifacademico.presentation.components.IFErrorState
import com.adielcalixto.ifacademico.presentation.components.IFIcon
import com.adielcalixto.ifacademico.presentation.dashboard.components.DashboardLoading
import com.adielcalixto.ifacademico.presentation.dashboard.components.TimetableLoading
import com.adielcalixto.ifacademico.presentation.components.IFSectionTitle
import com.adielcalixto.ifacademico.presentation.components.IFSlidingSheet
import com.adielcalixto.ifacademico.presentation.components.IFTextButton
import com.adielcalixto.ifacademico.presentation.dashboard.components.IFClassDetail
import com.adielcalixto.ifacademico.presentation.dashboard.components.IFClassRecord
import com.adielcalixto.ifacademico.presentation.dashboard.components.IFClassRow
import com.adielcalixto.ifacademico.presentation.dashboard.components.IFCoefficientPanel
import com.adielcalixto.ifacademico.presentation.dashboard.components.IFWeekdaySelector
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val state by viewModel.state.collectAsState()
    DashboardContent(state, viewModel::onAction)
}

@Composable
internal fun DashboardContent(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales[0]
    val homeScroll = rememberScrollState()
    val weeklyScroll = rememberScrollState()
    val detailScroll = rememberScrollState()
    val timetableFocus = remember { FocusRequester() }
    var homeOpener by remember { mutableStateOf(timetableFocus) }

    when {
        state.isLoading -> DashboardLoading(modifier)
        state.error != null -> ErrorComponent(state.error) {
            onAction(DashboardAction.LoadViewModelData)
        }
        else -> Column(
            modifier.fillMaxSize().verticalScroll(homeScroll)
                .padding(horizontal = IFTheme.pageInset, vertical = IFSpacing.lg)
        ) {
            Text(
                dateLabel(state.timeTable.today, locale) ?: stringResource(R.string.date_not_supplied),
                style = IFTheme.typography.labelSm,
                color = IFTheme.colors.primary
            )
            Spacer(Modifier.height(IFSpacing.lg))
            IFCoefficientPanel(
                coefficientLabel(state.performanceCoefficients.studentPerformance, locale),
                coefficientLabel(state.performanceCoefficients.classPerformance, locale)
            )
            Spacer(Modifier.height(IFSpacing.lg))
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(IFSpacing.xs),
                itemVerticalAlignment = Alignment.CenterVertically
            ) {
                IFSectionTitle(stringResource(R.string.today_classes))
                IFTextButton(
                    onClick = {
                        homeOpener = timetableFocus
                        onAction(DashboardAction.ShowIndividualTimeTable)
                    },
                    modifier = Modifier.focusRequester(timetableFocus)
                ) {
                    Text(stringResource(R.string.timetable))
                    androidx.compose.material3.Icon(IFIcon.Arrow.vector, null, Modifier.size(IFSize.icon))
                }
            }
            if (state.timeTable.classes.isEmpty()) {
                IFEmptyState(stringResource(R.string.no_classes_today))
            }
            sortedClasses(state.timeTable.classes).forEach { course ->
                val focus = remember(course) { FocusRequester() }
                IFClassRow(
                    course.displayRecord(locale),
                    onClick = {
                        homeOpener = focus
                        onAction(DashboardAction.ShowClass(course))
                    },
                    modifier = Modifier.focusRequester(focus)
                )
            }
            serverTimeLabel(state.timeTable.serverTime, locale)?.let {
                Text(
                    stringResource(R.string.server_time, it),
                    Modifier.fillMaxWidth().padding(top = IFSpacing.lg),
                    style = IFTheme.typography.caption,
                    color = IFTheme.colors.secondary,
                    textAlign = TextAlign.Right
                )
            }
        }
    }

    val sheet = state.sheet
    if (sheet != DashboardSheet.Closed) {
        val detail = sheet as? DashboardSheet.ClassDetail
        IFSlidingSheet(
            title = detail?.course?.className ?: stringResource(R.string.weekly_schedule),
            onDismiss = { onAction(DashboardAction.HideIndividualTimeTable) },
            returnFocus = homeOpener,
            scrollState = if (detail == null) weeklyScroll else detailScroll,
            titleStyle = if (detail == null) IFTheme.typography.headlineSm else IFTheme.typography.titleSm,
            maxHeightFraction = if (detail == null) .65f else .9f,
            showScrollIndicator = detail == null,
        ) {
            if (detail != null) {
                LaunchedEffect(detail.course) { detailScroll.scrollTo(0) }
                IFClassDetail(detail.course.displayRecord(locale))
            } else {
                when {
                    state.timetableLoading -> TimetableLoading(state.currentPeriod?.let {
                        stringResource(R.string.timetable_period, it.year, it.number)
                    })
                    state.timetableError != null -> IFErrorState(
                        state.timetableError.asUiText().asString(),
                        { onAction(DashboardAction.RetryTimetable) }
                    )
                    state.currentPeriod == null -> IFEmptyState(stringResource(R.string.no_academic_periods))
                    else -> {
                        Text(
                            stringResource(R.string.timetable_period, state.currentPeriod.year, state.currentPeriod.number),
                            style = IFTheme.typography.bodySm,
                            color = IFTheme.colors.secondary
                        )
                        Spacer(Modifier.height(IFSpacing.md))
                        IFWeekdaySelector(
                            timetableWeekdays.map { day -> weekdayLabel(day, locale, TextStyle.SHORT) },
                            timetableWeekdays.indexOf(state.selectedWeekday),
                            { index -> onAction(DashboardAction.SelectWeekday(timetableWeekdays[index])) }
                        )
                        val classes = sortedClasses(state.individualTimeTable?.classes?.get(state.selectedWeekday).orEmpty())
                        if (classes.isEmpty()) IFEmptyState(stringResource(R.string.empty_timetable_classes))
                        classes.forEach { course ->
                            IFClassRow(course.displayRecord(locale))
                        }
                    }
                }
            }
        }
    }
}

private fun weekdayLabel(day: Int, locale: Locale, style: TextStyle): String =
    DayOfWeek.of(if (day == 1) 7 else day - 1).getDisplayName(style, locale)

@Composable
private fun TimeTableClass.displayRecord(locale: Locale) = IFClassRecord(
    subject = className,
    start = startTime,
    end = endTime,
    room = classRoomName.takeIf { it.isNotBlank() } ?: stringResource(R.string.room_not_supplied),
    teacher = professorName.takeIf { it.isNotBlank() } ?: stringResource(R.string.teacher_not_supplied),
    finished = finished,
    period = academicPeriod.toString(),
    weekday = weekDay?.takeIf { it in 1..7 }?.let { weekdayLabel(it, locale, TextStyle.FULL) }
)
