package com.adielcalixto.ifacademico.presentation.diary_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.domain.entities.Diary
import com.adielcalixto.ifacademico.presentation.components.ErrorComponent
import com.adielcalixto.ifacademico.presentation.components.LoadingComponent
import com.adielcalixto.ifacademico.presentation.diary_list.components.PeriodsDropdown

fun Modifier.leftBorder(
    borderWidth: Dp,
    borderColor: Color,
    cornerRadius: Dp
): Modifier = this.drawBehind {
    val strokeWidth = borderWidth.toPx()
    val radius = cornerRadius.toPx()

    drawRoundRect(
        color = borderColor,
        size = Size(strokeWidth * 2, size.height),
        cornerRadius = CornerRadius(radius, radius),
        style = Stroke(width = strokeWidth * 2)
    )
}

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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .leftBorder(
                borderWidth = 2.dp,
                borderColor = MaterialTheme.colorScheme.primary,
                cornerRadius = 12.dp
            )
            .padding(start = 16.dp, top = 12.dp, end = 12.dp, bottom = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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
