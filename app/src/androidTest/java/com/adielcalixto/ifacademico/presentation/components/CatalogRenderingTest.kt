@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.adielcalixto.ifacademico.presentation.components

import com.adielcalixto.ifacademico.presentation.dashboard.DashboardPreview
import com.adielcalixto.ifacademico.presentation.dashboard.TimetablePreview
import com.adielcalixto.ifacademico.presentation.dashboard.ClassDetailPreview
import com.adielcalixto.ifacademico.presentation.diary_list.components.SubjectRowPreview
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.adielcalixto.ifacademico.presentation.calendar.components.CalendarPreview
import com.adielcalixto.ifacademico.presentation.dashboard.components.AcademicRowsPreview
import com.adielcalixto.ifacademico.presentation.diary_detail.components.AcademicDetailsPreview
import com.adielcalixto.ifacademico.presentation.student_info.components.ChromePreview
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Render the actual preview catalog and export review artifacts on the test device. */
class CatalogRenderingTest {
    @get:Rule val compose = createComposeRule()

    @Test fun lightCatalog() = renderCatalog("light", false, 1f, 412)
    @Test fun darkCatalog() = renderCatalog("dark", true, 1f, 412)
    @Test fun narrowEnlargedCatalog() = renderCatalog("narrow-large-text", false, 1.6f, 360)

    private fun renderCatalog(appearance: String, dark: Boolean, fontScale: Float, width: Int) {
        var selected by mutableIntStateOf(0)
        val names = listOf("controls", "chrome", "academic-rows", "academic-details", "calendar", "feedback", "sheet", "home", "timetable", "class-detail", "subjects")
        compose.setContent {
            val current = LocalConfiguration.current
            val configuration = remember(current) {
                Configuration(current).apply {
                    screenWidthDp = width
                    uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
                        if (dark) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
                }
            }
            val density = Density(LocalDensity.current.density, fontScale)
            CompositionLocalProvider(LocalConfiguration provides configuration, LocalDensity provides density) {
                Box(Modifier.width(width.dp)) {
                    key(selected) {
                        when (selected) {
                            0 -> ControlsPreview()
                            1 -> ChromePreview()
                            2 -> AcademicRowsPreview()
                            3 -> AcademicDetailsPreview()
                            4 -> CalendarPreview()
                            5 -> FeedbackPreview()
                            6 -> SheetPreview(width.dp)
                            7 -> DashboardPreview()
                            8 -> TimetablePreview()
                            9 -> ClassDetailPreview()
                            else -> SubjectRowPreview()
                        }
                    }
                }
            }
        }
        names.forEachIndexed { index, name ->
            compose.runOnIdle { selected = index }
            compose.waitForIdle()
            val image = if (name == "sheet") {
                val layouts = mutableListOf<TextLayoutResult>()
                compose.onNodeWithText("Subject details").performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
                    it(layouts)
                }
                assertEquals("Sheet inherits caller font scaling", fontScale, layouts.first().layoutInput.density.fontScale, .01f)
                compose.onNode(isDialog()).assertIsDisplayed().captureToImage()
            } else if (name == "timetable" || name == "class-detail") {
                compose.onNode(isDialog()).captureToImage()
            } else compose.onRoot().captureToImage()
            assertTrue("$appearance/$name has content", image.width > 0 && image.height > 0)
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val output = File(context.getExternalFilesDir("presentation-previews"), "$appearance-$name.png")
            output.outputStream().use { image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }
}
