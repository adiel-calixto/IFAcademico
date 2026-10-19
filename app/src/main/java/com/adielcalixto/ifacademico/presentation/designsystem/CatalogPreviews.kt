package com.adielcalixto.ifacademico.presentation.designsystem

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.adielcalixto.ifacademico.presentation.Theme
import java.time.LocalDate
import java.time.YearMonth

@Preview(name = "Light", widthDp = 412, heightDp = 1000, showBackground = true)
@Preview(name = "Dark", widthDp = 412, heightDp = 1000, uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Narrow, enlarged text", widthDp = 360, heightDp = 1200, fontScale = 1.6f, showBackground = true)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class IFCatalogPreviews

@Composable
private fun Catalog(content: @Composable ColumnScope.() -> Unit) {
    IFTheme {
        Surface(color = IFTheme.colors.background) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(IFTheme.pageInset),
                verticalArrangement = Arrangement.spacedBy(IFSpacing.lg), content = content)
        }
    }
}

@IFCatalogPreviews
@Composable
internal fun ControlsPreview() = Catalog {
    var text by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(true) }
    var theme by remember { mutableStateOf(Theme.SYSTEM) }
    IFSectionTitle("Controls")
    IFButton({}) { Text("Continue") }
    IFButton({}, enabled = false) { Text("Disabled") }
    IFButton({}, destructive = true) { Text("Log out") }
    IFTextButton({}) { Text("Secondary action") }
    IFOutlinedTextField(text, { text = it }, Modifier.fillMaxWidth(), label = { Text("Enrollment ID") })
    IFOutlinedTextField("Invalid input", {}, Modifier.fillMaxWidth(), label = { Text("Error") }, isError = true)
    IFThemeSelector(theme, { theme = it })
    IFUpdateSwitch(checked, { checked = it }, label = "Check for updates")
    IFUpdateSwitch(false, {}, enabled = false)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
        IFIcon.entries.forEach { icon -> IFIconButton({}, outlined = true) { Icon(icon.vector, icon.name) } }
    }
}

@IFCatalogPreviews
@Composable
internal fun ChromePreview() = Catalog {
    var selected by remember { mutableIntStateOf(0) }
    IFWordmark()
    IFHeader({})
    IFHeader({}, periodControl = { IFTextButton({}) { Text("2026 · 2") } })
    IFBottomNavigation(
        listOf(IFNavigationItem("Today", IFIcon.Home), IFNavigationItem("Grades", IFIcon.Grades),
            IFNavigationItem("Calendar", IFIcon.Calendar)), selected, { selected = it },
    )
    IFProfileIdentity("Ana Maria de Oliveira", "Computer Science and Information Systems")
    IFRecord("Enrollment ID", "202610012345")
    IFRecord("Account type", "Student")
    IFNotice("Your account requires a password change. Use your institution’s account service.")
    IFSettingsRow("Theme", "Choose an appearance or follow your device.") { IFThemeSelector(Theme.SYSTEM, {}) }
    IFSettingsRow("Check for updates", "Checks for new versions. You choose when to install updates.") {
        IFUpdateSwitch(true, {})
    }
}

private val SampleClass = IFClassRecord(
    "Introduction to algorithms and computational thinking", "08:00", "09:40", "Room 204", "Professor Maria Costa",
    finished = true, period = "2026 · 2",
)
private val SampleAssessment = IFAssessmentRecord(
    "Written assessment covering algorithms and complexity", "A1", "21/10/2026", "8.5", "10", "(A1 + A2) / 2",
)

@IFCatalogPreviews
@Composable
internal fun AcademicRowsPreview() = Catalog {
    IFCoefficientPanel("8.72", "7.94")
    IFSectionTitle("Today’s classes")
    IFClassRow(SampleClass, {})
    IFClassRow(SampleClass.copy(subject = "Databases", finished = false), {})
    IFSubjectRow(IFSubjectRecord("Introduction to algorithms and computational thinking", "Professor Maria Costa", 4, 80), {})
    IFClassDetail(SampleClass)
    IFTimetableContent("2026 · 2", listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"), 2, listOf(SampleClass), {}, {})
}

@IFCatalogPreviews
@Composable
internal fun AcademicDetailsPreview() = Catalog {
    IFAttendanceSummary(IFAttendanceRecord(2, 20, 18, 1, false))
    IFAttendanceSummary(IFAttendanceRecord(18, 20, 2, 1, true))
    IFAttendanceSummary(IFAttendanceRecord(0, 0, 0, 0, false))
    IFClassProgress(IFClassProgressRecord(80, 40, 32, 8))
    IFAssessments(listOf(
        IFAssessmentStage("Stage 1", listOf(SampleAssessment)),
        IFAssessmentStage("Stage 2", listOf(SampleAssessment.copy(grade = null, date = null, formula = null))),
    ))
}

@IFCatalogPreviews
@Composable
internal fun CalendarPreview() = Catalog {
    var month by remember { mutableStateOf(YearMonth.of(2026, 10)) }
    IFMonthGrid(month, LocalDate.of(2026, 10, 19),
        setOf(LocalDate.of(2026, 10, 19), LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 21)),
        { month = month.minusMonths(1) }, { month = month.plusMonths(1) })
    IFSectionTitle("Special dates")
    IFEventPanel("Assessment", "Written assessment covering algorithms and complexity", "21/10/2026",
        subject = "Algorithms", stage = "Stage 1", teacher = "Professor Maria Costa", room = "Room 204")
    IFEventPanel("Academic event", "Science and technology week", "19/10/2026 – 23/10/2026")
    IFEmptyState("No special dates listed for this month.")
}

@IFCatalogPreviews
@Composable
internal fun FeedbackPreview() = Catalog {
    IFLoadingPlaceholder()
    IFErrorState("Unable to load your academic records. Check your connection and try again.", {})
    IFEmptyState("No classes scheduled for this day.")
    IFToast("Appearance saved")
    IFConfirmationContent("Close your session and hide your academic records?", "Log out", {}, {})
    IFAssessments(emptyList())
    IFTimetableContent("2026 · 2", listOf("Mon", "Tue", "Wed"), 0, emptyList(), {}, {})
}

@IFCatalogPreviews
@Composable
internal fun SheetPreview(maxWidth: Dp = Dp.Unspecified) {
    IFTheme {
        var opened by remember { mutableStateOf(true) }
        val opener = remember { FocusRequester() }
        Surface(color = IFTheme.colors.background) {
            IFButton({ opened = true }, Modifier.focusRequester(opener).padding(24.dp)) { Text("Open details") }
        }
        if (opened) IFSlidingSheet("Subject details", { opened = false },
            modifier = if (maxWidth == Dp.Unspecified) Modifier else Modifier.widthIn(max = maxWidth),
            returnFocus = opener, onBack = {}) {
            IFClassDetail(SampleClass)
            Spacer(Modifier.height(IFSpacing.lg))
            IFAttendanceSummary(IFAttendanceRecord(18, 20, 2, 0, true))
            Spacer(Modifier.height(IFSpacing.lg))
            IFClassProgress(IFClassProgressRecord(80, 40, 32, 8))
            Spacer(Modifier.height(IFSpacing.lg))
            IFAssessments(listOf(IFAssessmentStage("Stage 1", listOf(SampleAssessment))))
        }
    }
}
