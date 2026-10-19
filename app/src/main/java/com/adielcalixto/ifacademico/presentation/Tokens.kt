package com.adielcalixto.ifacademico.presentation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Names and values mirror DESIGN.md; theme-specific values live in IFTheme. */
@Immutable
data class IFColors(
    val primary: Color, val onPrimary: Color, val primaryContainer: Color,
    val background: Color, val surface: Color, val surfaceSubtle: Color,
    val onSurface: Color, val secondary: Color, val outline: Color,
    val inputOutline: Color, val performanceSurface: Color,
    val performanceSecondary: Color, val primaryHover: Color, val error: Color,
    val notice: Color, val onNotice: Color, val scrim: Color,
    val toast: Color, val onToast: Color,
    val onPerformance: Color = Color.White,
)

internal val LightColors = IFColors(
    primary = Color(0xFF287343), onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F2EB), background = Color(0xFFF6F7F6),
    surface = Color.White, surfaceSubtle = Color(0xFFECEFED),
    onSurface = Color(0xFF252927), secondary = Color(0xFF626965),
    outline = Color(0xFFDDE1DE), inputOutline = Color(0xFFA5AFA8),
    performanceSurface = Color(0xFF292F2C), performanceSecondary = Color(0xFFD5DCD7),
    primaryHover = Color(0xFF174F2B), error = Color(0xFFA52B26),
    notice = Color(0xFFF2E9CC), onNotice = Color(0xFF624B16),
    scrim = Color(0x99101512), toast = Color(0xFF252927), onToast = Color.White,
)
internal val DarkColors = IFColors(
    primary = Color(0xFF68AD80), onPrimary = Color(0xFF14251A),
    primaryContainer = Color(0xFF25372B), background = Color(0xFF171A18),
    surface = Color(0xFF212522), surfaceSubtle = Color(0xFF2B302C),
    onSurface = Color(0xFFE8ECE9), secondary = Color(0xFFB0B8B2),
    outline = Color(0xFF363D38), inputOutline = Color(0xFF68746B),
    performanceSurface = Color(0xFF252C27), performanceSecondary = Color(0xFFCCD6CF),
    primaryHover = Color(0xFF80BF96), error = Color(0xFFFFB4AB),
    notice = Color(0xFF40391E), onNotice = Color(0xFFF4DFA4),
    scrim = Color(0xB3080B09), toast = Color(0xFFDDE5DF), onToast = Color(0xFF171A18),
)

object IFSpacing {
    val micro = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val panel = 20.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
    val pageInset = 24.dp
    val pageInsetNarrow = 20.dp
}
object IFShapes {
    val xs = RoundedCornerShape(4.dp)
    val brand = RoundedCornerShape(6.dp)
    val sm = RoundedCornerShape(8.dp)
    val md = RoundedCornerShape(12.dp)
    val lg = RoundedCornerShape(16.dp)
    val sheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val full = RoundedCornerShape(percent = 50)
}
object IFSize {
    val button = 48.dp
    val iconButton = 44.dp
    val touchTarget = 48.dp
    val icon = 20.dp
    val avatar = 56.dp
    val mark = 24.dp
    val themeSelect = 48.dp
    val switchWidth = 44.dp
    val switchHeight = 24.dp
}
object IFMotion {
    const val sheetMillis = 220
    const val toggleMillis = 180
}
