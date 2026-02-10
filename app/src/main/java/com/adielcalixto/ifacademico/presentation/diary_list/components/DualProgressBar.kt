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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times

@Composable
fun DualProgressBar(
    modifier: Modifier = Modifier,
    maxValue: Int = 100,
    firstProgress: Int = 0,
    secondProgress: Int = 0,
    firstColor: Color = Color(0xffb25047),
    secondColor: Color = Color(0xffb28547), // Yellow/Gold
    firstLabel: String = "First Progress",
    secondLabel: String = "Second Progress",
    backgroundColor: Color = Color.LightGray,
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
            // 0 label at start
            Text(
                text = "0",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            // First progress label
            if (validFirstProgress in 1..<maxValue) {
                Text(
                    text = validFirstProgress.toString(),
                    fontSize = 12.sp,
                    color = firstColor,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(
                            x = ((validFirstProgress.toFloat() / maxValue.toFloat()) *
                                    LocalConfiguration.current.screenWidthDp.dp * 0.65f)
                        )
                )
            }

            // Second progress label (total)
            if (validSecondProgress > 0) {
                Text(
                    text = validSecondProgress.toString(),
                    fontSize = 12.sp,
                    color = secondColor,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(
                            x = ((totalProgress.toFloat() / maxValue.toFloat()) *
                                    LocalConfiguration.current.screenWidthDp.dp * 0.7f)
                        )
                )
            }

            // Max value label at end
            Text(
                text = maxValue.toString(),
                fontSize = 12.sp,
                color = Color.Gray,
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