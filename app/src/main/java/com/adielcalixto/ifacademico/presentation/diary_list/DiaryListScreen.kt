package com.adielcalixto.ifacademico.presentation.diary_list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.Period
import com.adielcalixto.ifacademico.presentation.IFShapes
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.asUiText
import com.adielcalixto.ifacademico.presentation.components.*
import com.adielcalixto.ifacademico.presentation.diary_detail.DiaryDetailContent
import com.adielcalixto.ifacademico.presentation.diary_list.components.IFSubjectRecord
import com.adielcalixto.ifacademico.presentation.diary_list.components.IFSubjectRow

@Composable
fun GradesPeriodControl(state: DiaryListState, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.choose_academic_period)
    IFTextButton(onClick, modifier.semantics { contentDescription = description },
        enabled = state.periods.isNotEmpty()) {
        Text(state.selectedPeriod?.let { "${it.year}/${it.number}" } ?: stringResource(R.string.period))
        Icon(IFIcon.Down.vector, null, Modifier.size(IFSize.icon))
    }
}

@Composable
fun DiaryListScreen(viewModel: DiaryListViewModel, periodFocus: FocusRequester) {
    val state by viewModel.state.collectAsState()
    DiaryListContent(state, viewModel::onAction, periodFocus = periodFocus)
}

@Composable
internal fun DiaryListContent(
    state: DiaryListState,
    onAction: (DiaryListAction) -> Unit,
    modifier: Modifier = Modifier,
    periodFocus: FocusRequester? = null,
) {
    val locale = LocalConfiguration.current.locales[0]
    val listState = rememberLazyListState()
    val periodKey = state.selectedPeriod?.let { "${it.year}/${it.number}" }
    var displayedPeriod by rememberSaveable { mutableStateOf(periodKey) }
    var subjectOpener by remember { mutableStateOf<FocusRequester?>(null) }
    LaunchedEffect(periodKey) {
        if (displayedPeriod != periodKey) {
            listState.scrollToItem(0)
            displayedPeriod = periodKey
        }
    }

    // Keep the lazy list mounted under sheets and feedback so its saved position survives navigation.
    LazyColumn(
        modifier.fillMaxSize(), state = listState,
        contentPadding = PaddingValues(horizontal = IFTheme.pageInset, vertical = IFSpacing.lg)
    ) {
        when {
            state.isLoading -> item { IFLoadingPlaceholder() }
            state.error != null -> item {
                IFErrorState(state.error.asUiText().asString(), { onAction(DiaryListAction.LoadViewModelData) })
            }
            state.selectedPeriod == null -> item { IFEmptyState(stringResource(R.string.no_academic_periods)) }
            else -> {
                item(key = "period") {
                    FlowRow(Modifier.fillMaxWidth().padding(bottom = IFSpacing.xs),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalArrangement = Arrangement.spacedBy(IFSpacing.xs),
                        itemVerticalAlignment = Alignment.CenterVertically) {
                        IFSectionTitle("${state.selectedPeriod.year}/${state.selectedPeriod.number}")
                        Text(pluralStringResource(R.plurals.subject_count, state.diaries.size, state.diaries.size),
                            style = IFTheme.typography.bodySm, color = IFTheme.colors.secondary)
                    }
                }
                if (state.diaries.isEmpty()) item { IFEmptyState(stringResource(R.string.no_subjects)) }
                items(state.diaries, key = { it.id }) { diary ->
                    val focus = remember(diary.id) { FocusRequester() }
                    val assessments = state.assessments[diary.id]
                    IFSubjectRow(
                        IFSubjectRecord(
                            diary.name, diary.professor.ifBlank { stringResource(R.string.teacher_not_supplied) },
                            assessments?.takeIf { !it.isLoading && it.error == null }?.exams?.size, diary.hours
                        ),
                        onClick = {
                            subjectOpener = focus
                            onAction(DiaryListAction.ShowSubject(diary.id))
                        }, modifier = Modifier.focusRequester(focus)
                    )
                }
            }
        }
    }

    when (val sheet = state.sheet) {
        GradesSheet.Closed -> Unit
        GradesSheet.PeriodPicker -> PeriodPickerSheet(state, onAction, periodFocus)
        is GradesSheet.Subject -> {
            val diary = state.diaries.firstOrNull { it.id == sheet.diaryId }
            val period = state.selectedPeriod
            if (diary != null && period != null) key(diary.id, period) {
                IFSlidingSheet(diary.name, onDismiss = { onAction(DiaryListAction.CloseSheet) },
                    returnFocus = subjectOpener, titleStyle = IFTheme.typography.titleSm) {
                    DiaryDetailContent(diary, period, state.assessments[diary.id] ?: SubjectAssessments(), locale,
                        onRetryAssessments = { onAction(DiaryListAction.RetryAssessments(diary.id)) })
                }
            }
        }
    }
}

@Composable
private fun PeriodPickerSheet(state: DiaryListState, onAction: (DiaryListAction) -> Unit, periodFocus: FocusRequester?) {
    var pendingPeriod by remember { mutableStateOf<Period?>(null) }
    IFSlidingSheet(
        stringResource(R.string.choose_academic_period),
        onDismiss = {
            onAction(pendingPeriod?.let { DiaryListAction.SelectPeriod(it) } ?: DiaryListAction.CloseSheet)
        }, returnFocus = periodFocus, dismissRequested = pendingPeriod != null
    ) {
        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(IFSpacing.sm)) {
            state.periods.forEach { period ->
                val selected = period == state.selectedPeriod
                Surface(
                    modifier = Modifier.fillMaxWidth(), shape = IFShapes.md,
                    color = if (selected) IFTheme.colors.primaryContainer else IFTheme.colors.surface,
                    border = BorderStroke(1.dp, if (selected) IFTheme.colors.primary else IFTheme.colors.outline)
                ) {
                    Row(Modifier.fillMaxWidth().selectable(selected, role = Role.RadioButton) {
                        pendingPeriod = period
                    }.heightIn(min = IFSize.touchTarget).padding(IFSpacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(IFSpacing.sm)) {
                        Text("${period.year}/${period.number}", Modifier.weight(1f),
                            style = IFTheme.typography.labelMd)
                        Icon(if (selected) IFIcon.Check.vector else IFIcon.Chevron.vector, null,
                            Modifier.size(IFSize.icon), tint = IFTheme.colors.primary)
                    }
                }
            }
        }
    }
}
