---
version: alpha
name: IFAcademico
description: A restrained mobile academic design system with neutral surfaces, Manrope headings, and measured green accents in light and dark appearances.
colors:
  primary: "#287343"
  primary-dark: "#68ad80"
  on-primary: "#ffffff"
  on-primary-dark: "#14251a"
  primary-container: "#e8f2eb"
  primary-container-dark: "#25372b"
  background: "#f6f7f6"
  background-dark: "#171a18"
  surface: "#ffffff"
  surface-dark: "#212522"
  surface-subtle: "#ecefed"
  surface-subtle-dark: "#2b302c"
  on-surface: "#252927"
  on-surface-dark: "#e8ece9"
  secondary: "#626965"
  secondary-dark: "#b0b8b2"
  outline: "#dde1de"
  outline-dark: "#363d38"
  input-outline: "#a5afa8"
  input-outline-dark: "#68746b"
  performance-surface: "#292f2c"
  performance-surface-dark: "#252c27"
  on-performance: "#ffffff"
  performance-secondary: "#d5dcd7"
  performance-secondary-dark: "#ccd6cf"
  primary-hover: "#174f2b"
  primary-hover-dark: "#80bf96"
  error: "#a52b26"
  error-dark: "#ffb4ab"
  notice: "#f2e9cc"
  notice-dark: "#40391e"
  on-notice: "#624b16"
  on-notice-dark: "#f4dfa4"
  calendar-holiday-surface: "#f8e8e6"
  calendar-holiday-surface-dark: "#402b29"
  on-calendar-holiday: "#a52b26"
  on-calendar-holiday-dark: "#ffb4ab"
  calendar-event-surface: "#e8eef5"
  calendar-event-surface-dark: "#263340"
  on-calendar-event: "#345b7e"
  on-calendar-event-dark: "#9abbd8"
  calendar-assessment-surface: "#f0e9f3"
  calendar-assessment-surface-dark: "#372c3d"
  on-calendar-assessment: "#73517f"
  on-calendar-assessment-dark: "#c9abd5"
  scrim: "#10151299"
  scrim-dark: "#080b09b3"
  toast: "#252927"
  toast-dark: "#dde5df"
  on-toast: "#ffffff"
  on-toast-dark: "#171a18"
typography:
  headline-lg:
    fontFamily: Manrope
    fontSize: 28px
    fontWeight: 600
    lineHeight: 36px
    letterSpacing: 0px
  headline-md:
    fontFamily: Manrope
    fontSize: 24px
    fontWeight: 600
    lineHeight: 32px
    letterSpacing: 0px
  headline-sm:
    fontFamily: Manrope
    fontSize: 22px
    fontWeight: 600
    lineHeight: 30px
    letterSpacing: 0px
  title-lg:
    fontFamily: Manrope
    fontSize: 20px
    fontWeight: 600
    lineHeight: 28px
    letterSpacing: 0px
  title-md:
    fontFamily: Manrope
    fontSize: 18px
    fontWeight: 600
    lineHeight: 26px
    letterSpacing: 0px
  title-sm:
    fontFamily: Manrope
    fontSize: 16px
    fontWeight: 600
    lineHeight: 24px
    letterSpacing: 0px
  detail-title:
    fontFamily: Manrope
    fontSize: 16px
    fontWeight: 500
    lineHeight: 24px
    letterSpacing: 0px
  body-md:
    fontFamily: system-ui, sans-serif
    fontSize: 16px
    fontWeight: 400
    lineHeight: 1.5
  body-sm:
    fontFamily: system-ui, sans-serif
    fontSize: 14px
    fontWeight: 400
    lineHeight: 1.5
  label-md:
    fontFamily: system-ui, sans-serif
    fontSize: 14px
    fontWeight: 650
    lineHeight: 1.5
  label-sm:
    fontFamily: system-ui, sans-serif
    fontSize: 12px
    fontWeight: 600
    lineHeight: 1.5
  caption:
    fontFamily: system-ui, sans-serif
    fontSize: 11px
    fontWeight: 400
    lineHeight: 1.5
  coefficient:
    fontFamily: system-ui, sans-serif
    fontSize: 36px
    fontWeight: 500
    lineHeight: 44px
    letterSpacing: -1px
  coefficient-narrow:
    fontFamily: system-ui, sans-serif
    fontSize: 32px
    fontWeight: 500
    lineHeight: 40px
    letterSpacing: -1px
  coefficient-comparison:
    fontFamily: system-ui, sans-serif
    fontSize: 20px
    fontWeight: 400
    lineHeight: 28px
    letterSpacing: 0px
  wordmark:
    fontFamily: Manrope
    fontSize: 20px
    fontWeight: 750
    lineHeight: 1.5
    letterSpacing: -0.75px
spacing:
  micro: 4px
  xs: 8px
  sm: 12px
  md: 16px
  panel: 20px
  lg: 24px
  xl: 32px
  xxl: 48px
  page-inset: 24px
  page-inset-narrow: 20px
rounded:
  xs: 4px
  brand: 6px
  sm: 8px
  md: 12px
  lg: 16px
  sheet: 24px
  full: 9999px
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.on-primary}"
    typography: "{typography.label-md}"
    rounded: "{rounded.md}"
    padding: "{spacing.sm}"
    height: 48px
  button-primary-dark:
    backgroundColor: "{colors.primary-dark}"
    textColor: "{colors.on-primary-dark}"
  icon-button:
    textColor: "{colors.on-surface}"
    rounded: "{rounded.md}"
    width: 44px
    height: 44px
  navigation-selected:
    backgroundColor: "{colors.primary-container}"
    textColor: "{colors.primary}"
    rounded: "{rounded.md}"
  navigation-selected-dark:
    backgroundColor: "{colors.primary-container-dark}"
    textColor: "{colors.primary-dark}"
  performance-panel:
    backgroundColor: "{colors.performance-surface}"
    textColor: "{colors.on-performance}"
    rounded: "{rounded.lg}"
    padding: "{spacing.panel}"
  performance-panel-dark:
    backgroundColor: "{colors.performance-surface-dark}"
  event-row:
    textColor: "{colors.on-surface}"
    borderColor: "{colors.outline}"
    paddingVertical: "{spacing.micro}"
  event-row-dark:
    textColor: "{colors.on-surface-dark}"
    borderColor: "{colors.outline-dark}"
  attendance-summary:
    backgroundColor: "{colors.surface-subtle}"
    rounded: "{rounded.md}"
    padding: "{spacing.md}"
  attendance-summary-dark:
    backgroundColor: "{colors.surface-subtle-dark}"
  sliding-sheet:
    backgroundColor: "{colors.background}"
    textColor: "{colors.on-surface}"
    rounded: "{rounded.sheet}"
    padding: "{spacing.lg}"
  sliding-sheet-dark:
    backgroundColor: "{colors.background-dark}"
    textColor: "{colors.on-surface-dark}"
  theme-select:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.on-surface}"
    rounded: "{rounded.sm}"
    height: 48px
  update-switch:
    width: 44px
    height: 24px
    rounded: "{rounded.full}"
  avatar:
    backgroundColor: "{colors.surface-subtle}"
    textColor: "{colors.primary}"
    rounded: "{rounded.full}"
    size: 56px
---

# IFAcademico

## Overview

IFAcademico is a mobile academic companion for students checking classes, assessments, attendance, and academic dates. Its character is calm and practical. The interface should feel orderly enough for frequent checks without turning each screen into a dashboard of competing statistics.

Neutral backgrounds carry the content. Green identifies actions and selected states. Manrope gives headings and identity a clear, contemporary voice; system sans-serif keeps academic records familiar and readable. Information density is moderate, with compact metadata and deliberate space between sections.

Light and dark appearances share the same hierarchy. Dark mode uses charcoal surfaces and a moderately saturated green: neither pale mint nor vivid neon.

## Colors

The primary palette is Academic Green: `primary` in light mode and `primary-dark` in dark mode. Use it for interactive text, selected tabs, progress indicators, and small functional highlights. Soft Green, the `primary-container` pair, gives selected controls a quiet background.

The neutral palette separates Page Gray (`background`), Panel White or Charcoal (`surface`), and Inset Gray (`surface-subtle`). These broad surfaces remain neutral. Primary text uses `on-surface`; supporting text uses `secondary`. Fine separators use `outline`, while form controls use the stronger `input-outline`.

The coefficient panel uses Deep Charcoal (`performance-surface`) with white primary text and softer `performance-secondary` labels. It is a focused information anchor rather than a colored promotional banner.

Errors and logout use the `error` pair. Low attendance allowance uses the amber `notice` pair with `on-notice` text. Pair every status color with a label. Event colors may distinguish academic event types, but the description and type remain readable without the color cue.

Tokens without a `-dark` suffix define light mode. The matching suffixed token supplies the dark value. Shared values, such as white coefficient text, apply to both appearances. Focus outlines use the active primary color; interactive text uses the appropriate `primary-hover` value on hover.

## Typography

Use Manrope for headings, subject names, the wordmark, and the initials avatar. Use the platform system sans-serif for body copy, times, scores, and controls. The wordmark always reads **IFAcademico**, with that exact capitalization, without an accompanying icon.

Reserve `headline-lg` for a true page title when needed; the interface does not require a title above every screen. Profile identity uses `title-lg`; sliding panels use `headline-sm`. Home, timetable, and Grades subject rows share the 16sp medium-weight `detail-title` style. Section headings use the 16sp semibold `title-sm`; detail sections use `detail-title`. Headings use medium or semibold weights and neutral tracking so repeated academic records remain calm and easy to scan.

Body text uses `body-md`; supporting descriptions use `body-sm`. Labels and metadata are smaller and subordinate. Keep essential information readable when text is enlarged. Captions must not become the sole carrier of an action or error.

Student coefficient numerals use the medium-weight 36sp `coefficient` style, switching to the medium-weight 32sp `coefficient-narrow` style at 375dp and narrower. The class coefficient uses the regular-weight 20sp `coefficient-comparison` system-font style. Native sizes and line heights use sp and honor system font scaling, including enlarged text up to 200%. Keep times, grades, and their units together. Names and descriptions wrap naturally rather than shrinking to fit. Never use serif display type.

## Layout

Use a single-column mobile layout with a 4px spacing base. Main content has a 24px horizontal inset, reducing to 20px at 375px and narrower. Section gaps are usually 24px; related content uses 8–16px gaps. Panels have 16–20px internal padding.

The header and bottom navigation take their own space. The content region between them scrolls without passing behind either bar. Honor system safe areas. At wider widths, maintain a coherent mobile reading column rather than stretching academic rows across the entire viewport.

Three equal bottom navigation items are Today, Diaries, and Calendar. Profile opens from the header. The academic year/period control appears only on Diaries (Diários in Portuguese), with single-line `year/period` labels such as `2026/2`. Returning from Profile restores the previous location and useful scroll position.

Today begins with the date and coefficient panel, followed by today's classes. A Timetable action shares the section heading row with Today's classes. Grades begins with period context and a compact subject list. Calendar begins with month navigation and the month grid, followed by the full special-date list. Grades and Calendar have no redundant page titles.

Sliding panels hold secondary detail. They scroll independently, retain a visible close action, and keep titles and controls clear of system chrome.

Timetable and diary-detail panels show a quiet, persistent scroll thumb along the trailing edge of the body whenever content overflows. Use a rounded 3dp thumb, inset 6dp, in the secondary text color at 50% opacity, without a track. Its length and position reflect the visible content, with a 24dp minimum length capped to the body height. Keep it below the fixed header and above the bottom safe area; it is decorative and does not accept input.

## Elevation & Depth

Depth comes mainly from tonal separation, fine borders, and whitespace. Subject and class lists are flat, separated by thin rules. Calendar events form a flat list separated by fine rules. Avoid shadows around every row or panel.

Modal panels are distinguished by a translucent scrim and rounded top edge. A toast may use a soft shadow to separate temporary feedback from content. The presentation handset and its hardware shadows are not part of the product's visual system.

The three bottom pages form a horizontal pager in Today, Diaries, Calendar order.
Pages follow the finger and snap one page at a time without wrapping. Tab taps slide
through the same order over 240ms. The bottom navigation highlight tracks fractional
page progress during taps and swipes, with icon and label colors blending between
tabs. Accessible selection follows the nearest page; the Diaries period control
updates when movement settles. Header and bottom navigation remain stationary.
Vertical page scrolling remains independent. Back returns from Diaries or Calendar to
Today before leaving the app; Profile and modal panels close first.

Profile is outside the pager. It opens with a separate 220ms fade and 24dp horizontal
translation, reversed on return, preserving the prior tab and scroll position. Bottom
navigation remains available on Profile without a selected item; tapping a tab returns
to that page. Calendar month arrows animate the date cells with a 200ms fade and 24dp
directional translation. Arrows and weekday labels remain stationary. Horizontal
swipes on Calendar change pages; month changes use the arrows. Data arriving for the
same month does not replay the animation.

A sliding panel enters with a restrained 220ms ease-out translation and opacity change. Toggle feedback takes approximately 180ms. Reduce or remove movement when reduced motion is requested; motion must communicate a state change rather than decorate the screen.

## Shapes

The shape language is restrained and consistent. Small highlights use 4–8px corners; controls and event panels use 12px; the coefficient panel uses 16px. Sliding panels have 24px top corners with square lower edges at the viewport boundary.

Circles are reserved for avatars and status dots. Avoid pill-shaped containers for large content regions.

Functional icons belong to one outlined family with rounded caps and joins, a 24-unit view box, and a consistent 1.7px stroke. Most render at 18–22px. Icon controls have a minimum 44px touch target, and icons never replace accessible action names.

## Components

### Header and navigation

The header combines the wordmark, the contextual period control, and an outlined Profile button. Bottom tabs pair an icon with a short label. Selection uses green text and a subtle green background; inactive items remain neutral. Profile has its own back control instead of becoming a fourth bottom tab.

### Coefficient panel

Show the student coefficient as the dominant numeral and the class coefficient as a smaller comparison. Both are supplied values. The panel has no trend chart, class-range disclosure, assumed denominator, or decorative progress graphic.

### Class rows and weekly timetable

Class rows align start/end times in a narrow leading column, followed by subject, room, and teacher. Home and timetable class names use the 16sp/500 Manrope `detail-title` style, matching Grades subject names; class-detail sheet headings use the 16sp/600 `title-sm` style. Class-detail fields omit status. A subtle green rule separates time from content. A Finished label appears when applicable. Today rows show a trailing chevron for read-only detail; weekly timetable rows are static and omit the chevron.

The weekly timetable opens from the action beside Today's classes. It presents weekday selection and class rows in a sliding panel, with academic period context. The panel is capped at 65% of the window height, with a fixed heading and close control above a scrolling body. In the native app, the timetable uses the first period returned by the API independently of Grades selection. Weekly timetable rows do not open class details. The calendar page does not contain this schedule.

### Subject rows and grade details

Subject rows show name, teacher, assessment count, and workload hours, with a chevron. Names use the same 16sp/500 `detail-title` style as Home class rows. Keep the main list compact; tapping opens the detailed panel.

The panel uses a wrapping 16sp/600 `title-sm` subject heading, followed by teacher and period context, then Attendance and Assessments in that order. Attendance shows absences, maximum allowance, remaining allowance, and excused absences. A small progress bar is accompanied by numeric usage text. Low allowance uses a compact 12sp `label-sm` warning aligned right below the usage percentage, with amber `notice`/`on-notice` colors and 8dp horizontal/4dp vertical padding. The warning wraps at enlarged text sizes. The native
page warns at 80% of the allowance used. When the maximum is zero, show the supplied
counts without a usage percentage or progress bar.

The details panel is capped at 65% of the window height, with a fixed heading and close control above a scrolling body.

Workload hours remain in the subject list. Grade details omit a separate class-progress section.

Assessments are grouped by stage in API display order, with calculated averages
included within their stage. Compact flat rows show the 16sp description on the left and a
16sp semibold system-font labeled grade (`Grade: 8.5`) on the right, with a smaller
`Maximum: 10` below it. Acronym and date share a
14sp secondary metadata line. Ungraded records show “Not graded” with the maximum
below it. Rows stack the score below the description and metadata when available width
is below 280dp multiplied by the font scale; otherwise the score occupies at most 40%
of the row. Text wraps without truncation. Formula text sits in a subtle inset block when present. Missing results read “Not graded”; missing dates read “Date not supplied.” Academic detail remains read-only.

### Calendar and event rows

The localized month/year heading sits opposite previous/next controls, with unrestricted month navigation. Weekday labels and dates share a Sunday-first seven-column grid. Date cells are square, sized from their column width, with centered content and matching 4dp horizontal/vertical gutters. Single-line numeral leading accommodates enlarged text without imposing an interactive minimum height on static cells. Dates and event markers are static, not selectable. Events are indicated through a soft category highlight and underline; today retains a green outline. The grid flows directly into the special-date list without an explanatory legend.

Calendar colors adapt to each appearance: national and municipal holidays and school breaks use `calendar-holiday-surface`/`on-calendar-holiday` (red); vacations use `notice`/`on-notice` (amber); teaching stages, period boundaries, enrollment adjustment, pedagogical meetings, and teaching Saturdays use `primary-container`/`primary` (green). Sports, cultural, scientific, and other academic events use `calendar-event-surface`/`on-calendar-event` (blue). Final assessments use `calendar-assessment-surface`/`on-calendar-assessment` (plum). Diary submission, course-board meetings, and unknown types use `surface-subtle`/`secondary` (neutral). Category colors are theme tokens rather than API hex values. Date text and markers use the paired foreground. For overlapping events, the last event in displayed order supplies the color; accessible date descriptions include every distinct supplied event type. Colors do not add metadata or space to the compact list.

Below the grid, Special dates shows an entry count and a flat list with fine dividers. The native calendar uses the existing special-date endpoint, excluding routine Aula records; it does not aggregate Grades assessments. Include every event overlapping the month, ordered by start date and then description. Multi-day events appear once in the list and mark all their applicable days within the displayed month. Match the original compact calendar list: a narrow leading date column and a flexible description column, both at 14sp, with 4dp vertical padding and an 8dp column gap. Use day numbers within one month and short month names for ranges crossing month boundaries; expose the full localized range to accessibility services. Omit type, teacher, and room metadata from the rows. Preserve full descriptions and enlarged-text support. Empty months explain that no special dates are listed. Month controls and the unmarked grid remain available during loading and retryable failures; obsolete month responses cannot replace current content.

### Profile and settings

Profile keeps a centered initials avatar, student name, and course above a grouped preferences panel, with the project link below. Enrollment ID and account type are shown only when supplied by the account model. A required password-change state uses a notice when available. Keep content scrollable at enlarged text sizes. Settings omit helper text; the appearance selector and logout action align to the right. Profile has no separate page heading or Back control.

Theme selection offers System, Light, and Dark. Automatic updates uses a switch with a visible On/Off label. Settings labels persist, helper text stays subordinate, and the selected appearance applies consistently to panels, controls, overlays, and system foreground treatment.

Logout is visually distinct and opens a confirmation panel with a safe cancel action. The signed-out state offers a clear route back to authentication. Do not present an enabled preference as proof that an update has occurred.

### Feedback and accessibility

Buttons have visible focus and pressed states. Modal panels keep keyboard focus inside while open and return it to the opener on close. Selected navigation and toggle states are exposed through text and control semantics.

Session resolution uses only the theme background, a centered small progress indicator,
and a short localized label. Show the appropriate page and navigation after the
authentication state is known.

Academic loading uses static, subdued skeletons shaped like the content they replace:
coefficient values and class rows on Today, subject rows on Diaries, compact event
rows below the Calendar grid, and assessment rows inside subject details. Profile
loading follows the centered identity and grouped preferences layout. Preserve known
headings and period context; unavailable values and counts remain placeholders.
Skeletons use the same spacing and font-scaled line boxes as content, without shimmer
or pulsing. Decorative shapes expose no actions or academic values; each loading
region has one localized accessible indeterminate status. Keep Calendar navigation,
sheet dismissal, and already loaded attendance available during loading.

Use actionable retry messages for failures and descriptive empty states. Maintain readable contrast in each theme independently: at least 4.5:1 for ordinary text and 3:1 for large text and essential graphics.

## Do's and Don'ts

- Do keep large surfaces neutral and reserve green for interaction and focused emphasis.
- Do preserve the moderate dark green rather than returning to pastel or highly saturated alternatives.
- Do use Manrope headings, clear hierarchy, fine dividers, and consistent spacing.
- Do keep attendance first in grade details and workload hours in the subject list.
- Do show all special dates below the month grid and keep the timetable reachable from Today.
- Do preserve full descriptions, readable numeric values, accessible focus, and enlarged-text support.
- Don't add redundant Grades or Calendar titles, motivational headlines, or dashboard counters.
- Don't use serif headings, green-tinted page grounds, decorative gradients, or stacked ornamental cards.
- Don't turn calendar dates into selection controls or expand every assessment into the subject list.
- Don't imply historical coefficient data, individual absence dates, or editing actions that the academic records do not support.
