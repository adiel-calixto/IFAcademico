# Android design system

[DESIGN.md](../DESIGN.md) is the specification. Native Compose configuration lives directly in
`presentation/`. Shared controls live in `presentation/components/`; page-specific
components and previews live with their feature. `index.html` is the page redesign
reference, now implemented for Home.

## Theme and tokens

Use `AppTheme` at the application boundary: it observes saved appearance and updates
system foregrounds. Use the pure `IFTheme(darkTheme = ...)` provider in previews,
component tests, and isolated Compose content. It requires no Activity or Hilt.

Read semantic values through `IFTheme.colors`, `IFTheme.typography`,
`IFTheme.spacing`, and `IFTheme.shapes`. `IFSize` and `IFMotion` define dimensions
and durations. Layout values use dp, text uses sp. `IFTheme.pageInset` is 20dp at
375dp and narrower, otherwise 24dp. Host pages own safe areas and scrolling.

Manrope is bundled locally with its OFL license in `assets/licenses`. The heading
font defines 650, 700, and 750 weight instances of the variable font. Body and
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
`coefficient`, and `wordmark`. Keep ordinary borders on `outline` and form outlines
on the stronger `inputOutline`. Default elevated Material surfaces are neutral;
avoid adding tonal elevation to ordinary rows.

## Component ownership

- `presentation/`: `IFTheme`, semantic colors, typography, spacing, shapes, sizes,
  and motion configuration; `AppTheme` still observes saved appearance.
- `presentation/components/`: buttons, fields, icons, header/navigation, record and
  notice displays, loading/empty/retry feedback, confirmation content, and sheets.
  Notices are shared by profile and attendance displays.
- `dashboard/components/`: coefficient panel, class rows/details, weekday selector,
  and timetable content.
- `diary_list/components/`: subject rows; `diary_detail/components/`: attendance,
  class progress, and assessment displays.
- `calendar/components/`: month grid and event panels.
- `student_info/components/`: profile identity, settings rows, appearance selector,
  and update switch.

`IFSlidingSheet` keeps a fixed close/back header and an independently scrolling
body. Pass the opener's `FocusRequester` as `returnFocus`. An optional `scrollState`
lets a host retain separate scroll positions for nested contents. Close completes
after the exit animation; Back can return to parent content. Android animator
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
and 360dp enlarged-text variants. Sample data is local and never connects to an
institutional account. Switch to interactive preview for control behavior.

Home now follows the prototype: server date, charcoal coefficient panel, and flat
class rows. Timetable opens a weekday sheet; class details open from either Today
or the timetable, with nested Back navigation and focus restoration. Timetable
loading/retry is independent of Home. Server time always uses 24-hour `HH:mm`
format. Class rows use 17sp Manrope titles and class-detail sheets use 19sp
headings; status is omitted from the detail fields. Its period remains the first API period,
independent of Grades selection. Missing periods have an explicit empty state.

Other feature pages retain their existing layouts. Academic preview components
remain available in their owning features for subsequent redesigns. The old
`presentation/designsystem` package has been removed; the root `design-system/`
directory contains documentation only.

`DashboardPreviews.kt` provides Home, timetable, and class-detail variants. The
instrumentation preview renderer exports component and Home images into
`presentation-previews` under the test app's external files directory.

Verify changes with:

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest
# On a dedicated test device/emulator:
./gradlew :app:connectedDebugAndroidTest
```

Unit tests cover migration, persistence, observing preferences, theme resolution,
and ordinary-text contrast. Component tests cover selected/disabled states, switch
semantics, modal dismissal/focus return, and safe confirmation cancellation.
