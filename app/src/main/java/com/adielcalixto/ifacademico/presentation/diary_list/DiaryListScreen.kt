package com.adielcalixto.ifacademico.presentation.diary_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.domain.entities.Diary
import com.adielcalixto.ifacademico.domain.entities.Exam
import com.adielcalixto.ifacademico.presentation.components.ErrorComponent
import com.adielcalixto.ifacademico.presentation.components.LoadingComponent
import com.adielcalixto.ifacademico.presentation.diary_list.components.AbsencesSection
import com.adielcalixto.ifacademico.presentation.diary_list.components.ExamsSection
import com.adielcalixto.ifacademico.presentation.diary_list.components.PeriodsDropdown

@Composable
fun DiaryListScreen(viewModel: DiaryListViewModel) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        LoadingComponent()
        return
    }

    if (state.error != null) {
        ErrorComponent(state.error!!) {
            viewModel.onAction(
                DiaryListAction.LoadViewModelData
            )
        }
        return
    }

    Column {
        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
            item {
                PeriodsDropdown(state.periods, state.selectedPeriod) {
                    viewModel.onAction(
                        DiaryListAction.SelectPeriod(it)
                    )
                }
            }
            items(state.diaries) { diary ->
                DiaryItem(diary, state.examsMap[diary.id] ?: emptyList()) {
                    viewModel.onAction(
                        DiaryListAction.ExpandDiary(it)
                    )
                }
            }
        }
    }
}

@Composable
private fun DiaryItem(diary: Diary, exams: List<Exam>, onDiaryClick: (diaryId: Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                expanded = !expanded
                if (expanded)
                    onDiaryClick(diary.id)
            },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(diary.name, style = MaterialTheme.typography.titleSmall)
                    Text(
                        diary.professor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Light
                    )
                }
                Spacer(modifier = Modifier.padding(2.dp))

                val rotationAngle by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f
                )

                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Arrow",
                    modifier = Modifier
                        .rotate(rotationAngle)
                        .size(24.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                if (exams.isEmpty()) {
                    LinearProgressIndicator()
                } else {
                    Column {
                        AbsencesSection(diary.absences, diary.maxAbsences, diary.excusedAbsences)
                        ExamsSection(exams)
                    }
                }
            }
        }
    }
}