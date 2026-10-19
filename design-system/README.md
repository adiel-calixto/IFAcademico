# Android design system

[DESIGN.md](../DESIGN.md) is the specification. Native Compose implementation and
component previews live in `presentation/designsystem`. `index.html` is reserved
for the later page redesign.

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

## Components

- `Controls.kt`: buttons, icon controls, outlined fields, theme selector, update
  switch. Buttons grow with text size; switches retain a 48dp touch target around
  the 44×24dp visual. Pass an accessible action description to every icon control.
- `Chrome.kt`: wordmark, optional period slot in the header, navigation, initials,
  profile identity, labeled records, notices, and settings rows.
- `Academic.kt`: coefficient panel, class/detail/timetable content, subject rows,
  attendance, class progress, assessment stages/formulas, and event panels.
- `Calendar.kt`: static dates with supplied event markers, today's outline, and
  previous/next month callbacks. It does not fetch or aggregate events.
- `Sheet.kt`: independently scrolling modal body with a fixed close/back header.
  Retain the opener's `FocusRequester` and pass it as `returnFocus`. Close after
  the exit transition via `onDismiss`. `onBack` is for nested detail navigation.
  Compose respects Android animator duration scale, including disabled motion.
- `Feedback.kt`: loading placeholders, empty/retry states, toast, confirmation.

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

Open `CatalogPreviews.kt` in Android Studio. Every preview group has light, dark,
and 360dp enlarged-text variants. Sample data is local and never connects to an
institutional account. Switch to interactive preview for control behavior.

The current app adopts the foundation, icons, shared chrome, fields/buttons,
settings controls, and feedback. Academic catalog components are ready for the later
page redesign; this milestone does not move sections, change routes, add detail-sheet
navigation, or aggregate calendar assessments.

Verify changes with:

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest
# On a dedicated test device/emulator:
./gradlew :app:connectedDebugAndroidTest
```

Unit tests cover migration, persistence, observing preferences, theme resolution,
and ordinary-text contrast. Component tests cover selected/disabled states, switch
semantics, modal dismissal/focus return, and safe confirmation cancellation.
