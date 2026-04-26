package com.adielcalixto.ifacademico.presentation.diary_list.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DualProgressBar(
    modifier: Modifier = Modifier,
    maxValue: Int = 100,
    firstProgress: Int = 0,
    secondProgress: Int = 0,
    firstColor: Color = MaterialTheme.colorScheme.primary,
    secondColor: Color = MaterialTheme.colorScheme.tertiary,
    firstLabel: String = "First Progress",
    secondLabel: String = "Second Progress",
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    height: Dp = 6.dp,
) {
    val validFirstProgress = firstProgress.coerceIn(0, maxValue)
    val validSecondProgress = secondProgress.coerceIn(0, maxValue - validFirstProgress)
    val totalProgress = validFirstProgress + validSecondProgress

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            Text(
                text = "0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            if (validFirstProgress in 1..<maxValue) {
                Text(
                    text = validFirstProgress.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = firstColor,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = ((validFirstProgress.toFloat() / maxValue.toFloat()) * LocalConfiguration.current.screenWidthDp * 0.65f).dp)
                )
            }

            if (validSecondProgress > 0) {
                Text(
                    text = validSecondProgress.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = secondColor,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = ((totalProgress.toFloat() / maxValue.toFloat()) * LocalConfiguration.current.screenWidthDp * 0.7f).dp)
                )
            }

            Text(
                text = maxValue.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        // Progress bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawRect(
                color = backgroundColor,
                topLeft = Offset(0f, 0f),
                size = Size(canvasWidth, canvasHeight)
            )

            // Calculate widths for each segment
            val firstWidth = (validFirstProgress.toFloat() / maxValue.toFloat()) * canvasWidth
            val secondWidth = (validSecondProgress.toFloat() / maxValue.toFloat()) * canvasWidth

            if (validFirstProgress > 0) {
                drawRect(
                    color = firstColor,
                    topLeft = Offset(0f, 0f),
                    size = Size(firstWidth, canvasHeight)
                )
            }

            if (validSecondProgress > 0) {
                drawRect(
                    color = secondColor,
                    topLeft = Offset(firstWidth, 0f),
                    size = Size(secondWidth, canvasHeight)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LegendItem(
                color = firstColor,
                label = firstLabel,
            )

            LegendItem(
                color = secondColor,
                label = secondLabel,
            )
        }
    }
}

/**
 * A legend item showing a color indicator and label
 */
@Composable
private fun LegendItem(
    color: Color,
    label: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Color indicator box
        Canvas(modifier = Modifier.size(12.dp)) {
            drawRect(color = color)
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}