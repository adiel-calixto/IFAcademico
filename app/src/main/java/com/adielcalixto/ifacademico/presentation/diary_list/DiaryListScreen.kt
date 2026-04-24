package com.adielcalixto.ifacademico.presentation.diary_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.domain.entities.Diary
import com.adielcalixto.ifacademico.presentation.components.ErrorComponent
import com.adielcalixto.ifacademico.presentation.components.LoadingComponent
import com.adielcalixto.ifacademico.presentation.diary_list.components.PeriodsDropdown

@Composable
fun DiaryListScreen(
    viewModel: DiaryListViewModel,
    onDiaryClick: (diaryId: Int, periodNumber: Int, periodYear: Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        LoadingComponent()
        return
    }

    if (state.error != null) {
        ErrorComponent(state.error!!) {
            viewModel.onAction(DiaryListAction.LoadViewModelData)
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
                DiaryItem(
                    diary = diary,
                    onClick = {
                        val period = state.selectedPeriod
                        if (period != null) {
                            onDiaryClick(diary.id, period.number, period.year)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DiaryItem(
    diary: Diary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.MenuBook,
                contentDescription = "Diary",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = diary.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = diary.professor,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go to details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
