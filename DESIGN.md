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
  event-panel:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.on-surface}"
    rounded: "{rounded.md}"
    padding: "{spacing.md}"
  event-panel-dark:
    backgroundColor: "{colors.surface-dark}"
    textColor: "{colors.on-surface-dark}"
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

Use Manrope for headings, subject names, the wordmark, and the initials avatar. Use the platform system sans-serif for body copy, times, scores, and controls. The wordmark always reads **IFAcademico**, with that exact capitalization, alongside a compact IF mark.

Reserve `headline-lg` for a true page title when needed; the interface does not require a title above every screen. Profile identity uses `title-lg`; sliding panels use `headline-sm`. Home, timetable, and Grades subject rows share the 16sp medium-weight `detail-title` style. Section headings use the 16sp semibold `title-sm`; detail sections use `detail-title`. Headings use medium or semibold weights and neutral tracking so repeated academic records remain calm and easy to scan.

Body text uses `body-md`; supporting descriptions use `body-sm`. Labels and metadata are smaller and subordinate. Keep essential information readable when text is enlarged. Captions must not become the sole carrier of an action or error.

Student coefficient numerals use the medium-weight 36sp `coefficient` style, switching to the medium-weight 32sp `coefficient-narrow` style at 375dp and narrower. The class coefficient uses the regular-weight 20sp `coefficient-comparison` system-font style. Native sizes and line heights use sp and honor system font scaling, including enlarged text up to 200%. Keep times, grades, and their units together. Names and descriptions wrap naturally rather than shrinking to fit. Never use serif display type.

## Layout

Use a single-column mobile layout with a 4px spacing base. Main content has a 24px horizontal inset, reducing to 20px at 375px and narrower. Section gaps are usually 24px; related content uses 8–16px gaps. Panels have 16–20px internal padding.

The header and bottom navigation take their own space. The content region between them scrolls without passing behind either bar. Honor system safe areas. At wider widths, maintain a coherent mobile reading column rather than stretching academic rows across the entire viewport.

Three equal bottom navigation items are Today, Grades, and Calendar. Profile opens from the header. The academic year/period control appears only on Grades. Returning from Profile restores the previous location and useful scroll position.

Today begins with the date and coefficient panel, followed by today's classes. A Timetable action shares the section heading row with Today's classes. Grades begins with period context and a compact subject list. Calendar begins with month navigation and the month grid, followed by the full special-date list. Grades and Calendar have no redundant page titles.

Sliding panels hold secondary detail. They scroll independently, retain a visible close action, and keep titles and controls clear of system chrome.

## Elevation & Depth

Depth comes mainly from tonal separation, fine borders, and whitespace. Subject and class lists are flat, separated by thin rules. Calendar events sit on lightly bordered surfaces. Avoid shadows around every row or panel.

Modal panels are distinguished by a translucent scrim and rounded top edge. A toast may use a soft shadow to separate temporary feedback from content. The presentation handset and its hardware shadows are not part of the product's visual system.

A sliding panel enters with a restrained 220ms ease-out translation and opacity change. Toggle feedback takes approximately 180ms. Reduce or remove movement when reduced motion is requested; motion must communicate a state change rather than decorate the screen.

## Shapes

The shape language is restrained and consistent. Small highlights use 4–8px corners; controls and event panels use 12px; the coefficient panel uses 16px. Sliding panels have 24px top corners with square lower edges at the viewport boundary.

Circles are reserved for avatars and status dots. The IF mark is a 24px square with 6px corners. Avoid pill-shaped containers for large content regions.

Functional icons belong to one outlined family with rounded caps and joins, a 24-unit view box, and a consistent 1.7px stroke. Most render at 18–22px. Icon controls have a minimum 44px touch target, and icons never replace accessible action names.

## Components

### Header and navigation

The header combines the wordmark, the contextual period control, and an outlined Profile button. Bottom tabs pair an icon with a short label. Selection uses green text and a subtle green background; inactive items remain neutral. Profile has its own back control instead of becoming a fourth bottom tab.

### Coefficient panel

Show the student coefficient as the dominant numeral and the class coefficient as a smaller comparison. Both are supplied values. The panel has no trend chart, class-range disclosure, assumed denominator, or decorative progress graphic.

### Class rows and weekly timetable

Class rows align start/end times in a narrow leading column, followed by subject, room, and teacher. Home and timetable class names use the 16sp/500 Manrope `detail-title` style, matching Grades subject names; class-detail sheet headings use the 16sp/600 `title-sm` style. Class-detail fields omit status. A subtle green rule separates time from content. A Finished label appears when applicable. A trailing chevron indicates read-only detail.

The weekly timetable opens from the action beside Today's classes. It presents weekday selection and class rows in a sliding panel, with academic period context. In the native app, the timetable uses the first period returned by the API independently of Grades selection. Nested class details provide a return path to the timetable. The calendar page does not contain this schedule.

### Subject rows and grade details

Subject rows show name, teacher, assessment count, and workload hours, with a chevron. Names use the same 16sp/500 `detail-title` style as Home class rows. Keep the main list compact; tapping opens the detailed panel.

The panel starts with subject identity and context, then presents Attendance and Assessments in that order. Attendance shows absences, maximum allowance, remaining allowance, and excused absences. A small progress bar is accompanied by numeric usage text. Low allowance uses an explicit warning label as well as amber color. The native
page warns at 80% of the allowance used. When the maximum is zero, show the supplied
counts without a usage percentage or progress bar.

Workload hours remain in the subject list. Grade details omit a separate class-progress section.

Assessments are grouped by stage in API display order, with calculated averages
included within their stage. Each row balances description, acronym, and date
against grade and maximum score. Formula text sits in a subtle inset block when present. Missing results read “Not graded”; missing dates read “Date not supplied.” Academic detail remains read-only.

### Calendar and event panels

The month/year heading sits opposite previous/next controls. Weekday labels and dates share a seven-column grid. Dates are static, not selectable. Events are indicated through a soft highlight, underline, and dot; today has an outline.

Below the grid, Special dates lists every event or dated assessment overlapping the month, ordered by start date. Multi-day events appear once in the list and mark all their applicable days. Each panel shows a type label, description, date or range, and optional teacher and room information. Assessment entries also identify the subject and stage. Empty months explain that no special dates are listed.

### Profile and settings

Profile pairs an initials avatar with student name and course, followed by enrollment ID and account type. A required password-change state uses a notice.

Theme selection offers System, Light, and Dark. Automatic updates uses a switch with a visible On/Off label. Settings labels persist, helper text stays subordinate, and the selected appearance applies consistently to panels, controls, overlays, and system foreground treatment.

Logout is visually distinct and opens a confirmation panel with a safe cancel action. The signed-out state offers a clear route back to authentication. Do not present an enabled preference as proof that an update has occurred.

### Feedback and accessibility

Buttons have visible focus and pressed states. Modal panels keep keyboard focus inside while open and return it to the opener on close. Selected navigation and toggle states are exposed through text and control semantics.

Use subdued placeholder blocks while loading, actionable retry messages for failures, and descriptive empty states. Maintain readable contrast in each theme independently: at least 4.5:1 for ordinary text and 3:1 for large text and essential graphics.

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
