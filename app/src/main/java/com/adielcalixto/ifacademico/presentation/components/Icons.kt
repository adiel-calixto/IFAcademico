package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Small native vector family: 24-unit grid, rounded joins/caps, 1.7-unit stroke. */
enum class IFIcon(private val data: String) {
    Home("M3 10 L12 3 L21 10 M5 9 L5 21 L10 21 L10 14 L14 14 L14 21 L19 21 L19 9"),
    Grades("M4 3 L20 3 L20 21 L4 21 Z M8 8 L16 8 M8 12 L16 12 M8 16 L12 16"),
    Calendar("M3 5 L21 5 L21 21 L3 21 Z M7 3 L7 7 M17 3 L17 7 M3 10 L21 10 M7 14 L9 14 M15 14 L17 14 M7 18 L9 18"),
    Profile("M16 7 A4 4 0 1 1 8 7 A4 4 0 1 1 16 7 M4 21 L4 19 A8 7 0 0 1 20 19 L20 21"),
    Back("M15 5 L8 12 L15 19"),
    Chevron("M9 5 L16 12 L9 19"),
    Arrow("M4 12 L20 12 M14 6 L20 12 L14 18"),
    Down("M5 9 L12 16 L19 9"),
    Close("M6 6 L18 18 M18 6 L6 18"),
    Check("M4 12 L9 17 L20 6"),
    Logout("M9 4 L4 4 L4 20 L9 20 M10 12 L21 12 M16 7 L21 12 L16 17"),
    Visibility("M2 12 Q12 -1 22 12 Q12 25 2 12 Z M15 12 A3 3 0 1 1 9 12 A3 3 0 1 1 15 12"),
    VisibilityOff("M3 3 L21 21 M9 5 Q17 3 22 12 Q20 15 18 17 M6 7 Q3 9 2 12 Q8 21 15 18"),
    Warning("M12 3 L22 21 L2 21 Z M12 9 L12 14 M12 17 L12 17.1"),
    Update("M20 8 A9 9 0 1 0 20 17 M20 3 L20 8 L15 8 M12 7 L12 12 L15 14"),
    Info("M21 12 A9 9 0 1 1 3 12 A9 9 0 1 1 21 12 M12 11 L12 17 M12 7 L12 7.1"),
    Clock("M21 12 A9 9 0 1 1 3 12 A9 9 0 1 1 21 12 M12 6 L12 12 L16 14"),
    Room("M19 9 Q19 15 12 22 Q5 15 5 9 A7 7 0 1 1 19 9 M15 9 A3 3 0 1 1 9 9 A3 3 0 1 1 15 9"),
    Code("M8 5 L2 12 L8 19 M16 5 L22 12 L16 19 M14 3 L10 21"),
    ;

    val vector: ImageVector by lazy {
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f,
            autoMirror = this == Arrow || this == Back || this == Chevron || this == Logout).apply {
            addPath(
                pathData = PathParser().parsePathString(data).toNodes(),
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.7f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round,
            )
        }.build()
    }
}
