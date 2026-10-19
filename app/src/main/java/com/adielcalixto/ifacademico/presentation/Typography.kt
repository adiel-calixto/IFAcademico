package com.adielcalixto.ifacademico.presentation

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.adielcalixto.ifacademico.R

@OptIn(ExperimentalTextApi::class)
internal val Manrope = FontFamily(
    listOf(500, 600, 750).map { weight ->
        Font(
            R.font.manrope,
            FontWeight(weight),
            variationSettings = FontVariation.Settings(FontVariation.weight(weight))
        )
    }
)

private fun heading(size: Int, weight: Int, lineHeight: Int, tracking: Float = 0f) = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight(weight),
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking.sp,
)

private fun system(
    size: Int,
    weight: Int = 400,
    lineHeight: Float = size * 1.5f,
    tracking: Float = 0f,
) = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight(weight),
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking.sp,
)

@Immutable
class IFTypography internal constructor() {
    val headlineLg = heading(28, 600, 36)
    val headlineMd = heading(24, 600, 32)
    val headlineSm = heading(22, 600, 30)
    val titleLg = heading(20, 600, 28)
    val titleMd = heading(18, 600, 26)
    val titleSm = heading(16, 600, 24)
    val detailTitle = heading(16, 500, 24)
    val bodyMd = system(16)
    val bodySm = system(14)
    val labelMd = system(14, 650)
    val labelSm = system(12, 600)
    val caption = system(11)
    val coefficient = system(36, weight = 500, lineHeight = 44f, tracking = -1f)
    val coefficientNarrow = system(32, weight = 500, lineHeight = 40f, tracking = -1f)
    val coefficientComparison = system(20, lineHeight = 28f)
    val wordmark = heading(20, 750, 30, -.75f)

    internal val material = Typography(
        displayLarge = headlineLg, displayMedium = headlineMd, displaySmall = headlineSm,
        headlineLarge = headlineLg, headlineMedium = headlineMd, headlineSmall = headlineSm,
        titleLarge = titleLg, titleMedium = titleMd, titleSmall = titleSm,
        bodyLarge = bodyMd, bodyMedium = bodySm, bodySmall = caption,
        labelLarge = labelMd, labelMedium = labelSm, labelSmall = caption,
    )
}
