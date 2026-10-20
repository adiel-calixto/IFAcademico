# Android design system

[DESIGN.md](../DESIGN.md) is the specification. Native Compose configuration lives directly in
`presentation/`. Shared controls live in `presentation/components/`; page-specific
components and previews live with their feature. `index.html` is the page redesign
reference, now implemented for Home, Grades, Calendar, and Profile.

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

Timetable and subject-detail sheets opt into `showScrollIndicator`. A persistent,
noninteractive 3dp rounded thumb sits 6dp from the body's trailing edge, using
`secondary` at 50% opacity without a track. It reflects the visible proportion
and scroll position, with a 24dp minimum length capped to the viewport. It is
hidden when content fits and stays below the fixed header and above the bottom
safe area. The default is off for other sheets.

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

## Page motion and state

`SignedInShell` owns the stationary header/bottom navigation, a saveable
`HorizontalPager` in `MainTab` order, and separate Profile navigation. Tab taps use
`IFMotion.tabMillis` (240ms), cancel earlier tab animations, and slide through
intermediate pages. Swipes use standard pager snapping. Selection and the Diaries
period control follow `settledPage`; page switches do not build a back stack.
Back returns to Today before leaving the app. Sheets retain modal gesture and Back
handling. Profile uses a 220ms fade and 24dp translation, reversed when closing.

The outer signed-in route owns feature view models. The page and header share the
Diaries model; lazy page disposal and Profile visits preserve model data and saveable
scroll positions. Logout removes this owner; a new login starts on Today. The pager
selection survives configuration changes, while academic-data restoration after
process death follows the existing feature behavior.

`IFMonthGrid` animates date cells with a 200ms fade and directional 24dp movement.
Weekday labels and arrow controls remain stationary. Each outgoing grid retains its
month/marker snapshot and is excluded from accessibility semantics; marker or loading
changes within a month do not restart motion. Android animator duration scale applies
to programmatic transitions, including zero; direct finger dragging remains enabled.

## Reviewing and extending

Session loading uses `IFSessionLoading`: a centered progress indicator and localized
label without page placeholders or signed-in chrome. Academic loading uses
feature-owned skeleton layouts built from `IFSkeletonLine` and `IFSkeletonBlock`.
Lines reserve typography-based, font-scaled height. `IFLoadingRegion` provides a
single localized indeterminate loading status while preserving real heading
semantics. Skeleton shapes are static and noninteractive; they contain no fake data.
Known period context, Calendar controls, sheet close controls, and loaded attendance
remain available. Home's timetable action is disabled during initial Home loading.

`LoadingPreview.kt` covers session, Home, Diaries, Calendar, timetable, assessments,
and Profile loading in light/dark and narrow 160%/200% text variants. The existing
instrumentation renderer exports these variants, including a loading-only 200%
catalog. Loading interaction tests cover accessibility, content/error transitions,
and sheet dismissal during a request.

Open the feature and shared `*Preview.kt` files in Android Studio. Preview groups have light, dark,
and 360dp enlarged-text variants at 160% and 200%. Sample data is local and never connects to an
institutional account. Switch to interactive preview for control behavior.

Home now follows the prototype: server date, charcoal coefficient panel, and flat
class rows. Timetable opens a weekday sheet capped at 65% of the window height,
with a fixed header and scrolling body, static class rows and no detail action or
chevron. Class details open from Today, with focus restoration on dismissal. Timetable
loading/retry is independent of Home. Server time always uses 24-hour `HH:mm`
format and is right-aligned below today's classes. The header wordmark displays
the app name without an accompanying icon. Home, timetable, and Grades rows share 16sp/500 Manrope subject names;
class-detail sheets use 16sp/600 headings; status is omitted from the detail fields.
Its period remains the first API period, independent of Grades selection.
Missing periods have an explicit empty state.

The Diaries page (Diários in Portuguese) uses a flat subject list with period context
and a header period picker. Picker entries use a single `year/period` label such as `2026/2`.
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

Subject sheets use a wrapping 16sp/600 `titleSm` heading and present attendance
and assessments in that order, capped at 65% of the window height with a fixed
header and scrolling body; workload hours remain in subject rows. There is no
separate class-progress section. The attendance warning uses compact, right-aligned
12sp `labelSm` text in the amber notice colors below the usage percentage.
Assessments use flat rows with a 16sp description, combined acronym/date metadata,
and a 16sp semibold system-font labeled grade with a smaller maximum below it. Missing grades retain a
separate “Not graded” label and maximum. Below 280dp multiplied by the font scale,
scores move below descriptions and metadata; wider rows limit scores to 40% of the
available width. Formulas remain in subtle inset blocks. Shared sheet and notice
defaults are unchanged.
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
tab/Profile navigation; changing months resets scroll.

Profile preserves the centered student identity, grouped preferences panel, and
bottom project link from the original page. It uses an initials avatar, semantic
typography and neutral surfaces. Settings omit helper text and use a compact,
right-aligned appearance selector and logout action. Content scrolls at small heights and enlarged text sizes; the project
link rests at the bottom when space permits. Profile has no separate page heading
or Back control. Logout opens the shared confirmation sheet, with cancellation
and focus restoration. Only name and course are displayed: the current Student
model does not supply an enrollment number, account type, or password-change state;
its internal registration ID is not presented as an enrollment number.
Academic preview components remain available in their owning features for subsequent redesigns. The old
`presentation/designsystem` package has been removed; the root `design-system/`
directory contains documentation only.

`DashboardPreviews.kt` provides Home, timetable, and class-detail variants.
`GradesPreviews.kt` provides full-page, period-picker, and subject-detail variants.
`CalendarPreview.kt` provides full-page and empty Calendar variants with long descriptions
and events spanning month boundaries.
`StudentInfoPreview.kt` provides a full-page Profile in light, dark, and enlarged-text variants.
The instrumentation preview renderer exports component, Home, Grades, Calendar, and Profile images
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
