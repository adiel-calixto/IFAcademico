package com.adielcalixto.ifacademico.presentation.designsystem

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
    listOf(650, 700, 750).map { weight ->
        Font(R.font.manrope, FontWeight(weight),
            variationSettings = FontVariation.Settings(FontVariation.weight(weight)))
    }
)
private fun heading(size: Int, weight: Int, line: Float, tracking: Float = 0f) = TextStyle(
    fontFamily = Manrope, fontWeight = FontWeight(weight), fontSize = size.sp,
    lineHeight = (size * line).sp, letterSpacing = tracking.sp,
)
private fun system(size: Int, weight: Int = 400, line: Float = 1.5f, tracking: Float = 0f) = TextStyle(
    fontFamily = FontFamily.SansSerif, fontWeight = FontWeight(weight), fontSize = size.sp,
    lineHeight = (size * line).sp, letterSpacing = tracking.sp,
)
@Immutable
class IFTypography internal constructor() {
    val headlineLg = heading(34, 650, 1.2f, -1f)
    val headlineMd = heading(26, 650, 1.2f, -1f)
    val headlineSm = heading(24, 650, 1.3f, -.5f)
    val titleLg = heading(22, 700, 1.4f)
    val titleMd = heading(20, 700, 1.4f, -.4f)
    val titleSm = heading(19, 700, 1.4f, -.4f)
    val detailTitle = heading(17, 700, 1.45f)
    val bodyMd = system(16)
    val bodySm = system(14)
    val labelMd = system(14, 650)
    val labelSm = system(12, 600)
    val caption = system(11)
    val coefficient = system(52, 500, 1.2f, -3f)
    val coefficientNarrow = system(40, 500, 1.2f, -3f)
    val wordmark = heading(20, 750, 1.5f, -.75f)

    internal val material = Typography(
        displayLarge = headlineLg, displayMedium = headlineMd, displaySmall = headlineSm,
        headlineLarge = headlineLg, headlineMedium = headlineMd, headlineSmall = headlineSm,
        titleLarge = titleLg, titleMedium = titleMd, titleSmall = titleSm,
        bodyLarge = bodyMd, bodyMedium = bodySm, bodySmall = caption,
        labelLarge = labelMd, labelMedium = labelSm, labelSmall = caption,
    )
}
