# Android design system

[DESIGN.md](../DESIGN.md) is the specification. Native Compose configuration lives directly in
`presentation/`. Shared controls live in `presentation/components/`; page-specific
components and previews live with their feature. `index.html` is the page redesign
reference, now implemented for Home, Grades, and Calendar.

## Theme and tokens

Use `AppTheme` at the application boundary: it observes saved appearance and updates
system foregrounds. Use the pure `IFTheme(darkTheme = ...)` provider in previews,
component tests, and isolated Compose content. It requires no Activity or Hilt.

Read semantic values through `IFTheme.colors`, `IFTheme.typography`,
`IFTheme.spacing`, and `IFTheme.shapes`. `IFSize` and `IFMotion` define dimensions
and durations. Layout values use dp, text uses sp. `IFTheme.pageInset` is 20dp at
375dp and narrower, otherwise 24dp. Host pages own safe areas and scrolling.

Manrope is bundled locally with its OFL license in `assets/licenses`. The heading
font defines genuine 500 and 600 weight instances of the variable font for subject
names and headings, plus 750 for the unchanged wordmark. Body and
numeric styles use Android system sans-serif. There are no runtime font downloads.

The Material bridge keeps existing screens usable during migration:

| Material role | Design token |
| --- | --- |
| headlineLarge / Medium / Small | headlineLg / Md / Sm |
| titleLarge / Medium / Small | titleLg / Md / Sm |
| bodyLarge / Medium / Small | bodyMd / bodySm / caption |
| labelLarge / Medium / Small | labelMd / labelSm / caption |
| surfaceVariant | surfaceSubtle |
| onSurfaceVariant | secondary |
| outline / outlineVariant | inputOutline / outline |
| inverseSurface / inverseOnSurface | toast / onToast |

Use the named design styles directly for new components, especially `detailTitle`,
`coefficient`, `coefficientNarrow`, `coefficientComparison`, and `wordmark`.
Keep ordinary borders on `outline` and form outlines on the stronger `inputOutline`.
Default elevated Material surfaces are neutral;
avoid adding tonal elevation to ordinary rows.

### Typography hierarchy

Headlines use 28/36, 24/32, and 22/30sp size/line-height pairs at weight 600.
Titles use 20/28, 18/26, and 16/24sp at weight 600. `detailTitle` uses
16/24sp at weight 500 for subject rows and detail sections. Headings have neutral
tracking; avoid local bold overrides that bypass the shared hierarchy.

The student coefficient uses system sans-serif at 36/44sp, weight 500, and -1sp
tracking; `coefficientNarrow` uses 32/40sp at weight 500 at 375dp and narrower.
`coefficientComparison` uses 20/28sp, weight 400, and neutral tracking for the class
coefficient. Body text, metadata, controls, and branding retain their existing
styles. Text wraps naturally and respects system font scaling; review narrow
layouts with enlarged text up to 200%.

## Component ownership

- `presentation/`: `IFTheme`, semantic colors, typography, spacing, shapes, sizes,
  and motion configuration; `AppTheme` still observes saved appearance.
- `presentation/components/`: buttons, fields, icons, header/navigation, record and
  notice displays, loading/empty/retry feedback, confirmation content, and sheets.
  Notices are shared by profile and attendance displays.
- `dashboard/components/`: coefficient panel, class rows/details, weekday selector,
  and timetable content.
- `diary_list/components/`: subject rows; `diary_detail/components/`: attendance
  and assessment displays.
- `calendar/components/`: static month grid and flat event rows.
- `student_info/components/`: profile identity, settings rows, appearance selector,
  and update switch.

`IFSlidingSheet` keeps a fixed close/back header and an independently scrolling
body. Pass the opener's `FocusRequester` as `returnFocus`. An optional `scrollState`
lets a host retain separate scroll positions for nested contents. Close completes
after the exit animation; `dismissRequested` lets a selection request the same animated
close before its host applies the selection. Back can return to parent content. Android animator
scale, including disabled animation, is respected.

Components take display records and callbacks, never view models, repositories,
or navigation controllers. Callers supply formatted dates/numbers, assessment
stages, every marked calendar date, and attendance warning state. A null assessment
grade is “Not graded”; a null date is “Date not supplied”. A zero grade supplied as
`"0"` remains a grade. No component guesses academic data from missing values.

`IFRecord`, `IFProfileIdentity`, and `IFNotice` compose the profile's enrollment,
account-type, and required-password-change states. Omit unavailable optional records;
do not invent values absent from the current API models.

Appearance selection is System / Light / Dark. Fresh or unknown preferences resolve
to System; persisted Purple migrates to Dark, Yellow/Blue to Light. Existing update
preferences are preserved. The update switch controls checking for releases; it
does not install updates or prove that an update occurred.

## Reviewing and extending

Open the feature and shared `*Preview.kt` files in Android Studio. Preview groups have light, dark,
and 360dp enlarged-text variants at 160% and 200%. Sample data is local and never connects to an
institutional account. Switch to interactive preview for control behavior.

Home now follows the prototype: server date, charcoal coefficient panel, and flat
class rows. Timetable opens a weekday sheet; class details open from either Today
or the timetable, with nested Back navigation and focus restoration. Timetable
loading/retry is independent of Home. Server time always uses 24-hour `HH:mm`
format and is right-aligned below today's classes. The header wordmark displays
the app name without an accompanying icon. Home, timetable, and Grades rows share 16sp/500 Manrope subject names;
class-detail sheets use 16sp/600 headings; status is omitted from the detail fields.
Its period remains the first API period, independent of Grades selection.
Missing periods have an explicit empty state.

Grades now uses a flat subject list with period context and a header period picker.
Both the picker and subject details use sliding sheets with focus restoration. The
selected period and list scroll position survive navigation to Profile and other tabs;
changing periods resets the list. Selection affects Grades only, independently of
Home's timetable. The first API period is selected initially, with explicit states for
missing periods, empty subjects, loading, and failures.

Subjects appear before assessment counts finish loading. Up to four assessment
requests run at once; successful responses supply both counts and detail content.
Counts include averages returned by the API. Pending/unavailable counts show only
workload hours. Assessment errors have a local retry inside subject details while
attendance remains available. Obsolete period/registration
responses cannot replace current state.

Subject sheets present attendance and assessments in that order; workload hours
remain in subject rows. There is no separate class-progress section.
Attendance uses supplied counts and warns at 80% allowance usage; a zero maximum
omits the percentage and progress bar. Assessments follow API display order and
are grouped by stage, with averages inline and supplied formulas visible. Numbers
and dates use the device locale. Nullable API/cached grades preserve zero as a grade.
The previous full-screen detail route and legacy grade widgets have been removed.

Calendar now uses a Sunday-first, non-interactive month grid with square date
cells sized from the available column width, matching 4dp gutters, centered
numerals, and previous/next
controls, passive category-colored highlights and underlines, and a today outline.
There are no event dots or explanatory legend below the grid. Holiday/break dates use amber notice tokens; academic dates use green;
events use blue `calendarEventSurface`/`onCalendarEvent`; final assessments use
plum `calendarAssessmentSurface`/`onCalendarAssessment`; administrative and unknown
types use neutral tokens. Every appearance has paired readable foregrounds.
The caller supplies `IFMonthGrid` with a map of dates to `IFCalendarDayMarker`
display records. The last listed overlapping event supplies its day's color;
all distinct event types remain in accessible date descriptions. Below it,
a single scrolling page shows compact flat special-date rows with an entry count.
Rows match the original compact list: a narrow date column beside the full
14sp description, 4dp vertical padding, and an 8dp column gap. Dates use day
numbers or short month names for cross-month ranges, with full localized date
ranges exposed to accessibility services. Extra metadata is omitted. Events overlapping the month mark
each applicable day and appear once; routine `Aula` records are excluded. Calendar
uses its existing endpoint independently of Grades and does not aggregate assessments.
The grid and navigation remain available during loading and retryable failures;
late responses cannot replace the current month. Month and scroll position survive
tab/Profile navigation; changing months resets scroll. Profile retains its existing layout.
Academic preview components remain available in their owning features for subsequent redesigns. The old
`presentation/designsystem` package has been removed; the root `design-system/`
directory contains documentation only.

`DashboardPreviews.kt` provides Home, timetable, and class-detail variants.
`GradesPreviews.kt` provides full-page, period-picker, and subject-detail variants.
`CalendarPreview.kt` provides full-page and empty Calendar variants with long descriptions
and events spanning month boundaries.
The instrumentation preview renderer exports component, Home, Grades, and Calendar images
into `presentation-previews` under the test app's external files directory.

Verify changes with:

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest
# On a dedicated test device/emulator:
./gradlew :app:connectedDebugAndroidTest
```

Unit tests cover migration, persistence, observing preferences, theme resolution,
ordinary-text contrast, Grades loading/retry and stale responses, assessment
serialization/formatting, attendance warning boundaries, Calendar range clipping/formatting,
category palettes and contrast, and month loading/retry with stale-response protection. Component tests cover
selected/disabled states, switch semantics, modal dismissal/focus return, Grades
period selection and retained list position, Calendar navigation, static dates/rows,
retained scroll position, retry feedback, and safe confirmation cancellation.
