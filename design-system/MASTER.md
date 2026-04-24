# IFAcadêmico Design System

## Overview
- **Product Type:** Academic Student Dashboard (Android)
- **Style:** Minimal, clean, mobile-first
- **Industry:** Education
- **Platform:** Jetpack Compose (Android)

## Color Palette

### Primary (Green - #4E7B4C)
Primary green serves as the main brand color. Dark mode uses the exact green `#4E7B4C`, while light mode uses a lighter variant `#6B9B6E` (approximately 20% lighter for proper contrast on light backgrounds).

| Token | Dark Mode | Light Mode | Usage |
|-------|-----------|------------|-------|
| **Primary** | `#4E7B4C` | `#6B9B6E` | Main brand color, buttons, links |
| **On Primary** | `#FFFFFF` | `#FFFFFF` | Text/icons on primary |
| **Primary Container** | `#3E673F` | `#E8F0E7` | Container backgrounds |
| **On Primary Container** | `#E0E0E0` | `#1A3A1F` | Text on primary containers |

### Secondary
| Token | Dark Mode | Light Mode | Usage |
|-------|-----------|------------|-------|
| **Secondary** | `#4A5568` | `#718096` | Secondary actions, subtitles |
| **On Secondary** | `#FFFFFF` | `#FFFFFF` | Text on secondary |
| **Secondary Container** | `#2D3748` | `#E2E8F0` | Secondary containers |
| **On Secondary Container** | `#E0E0E0` | `#1A202C` | Text on secondary containers |

### Tertiary (Accent - Coral)
| Token | Dark Mode | Light Mode | Usage |
|-------|-----------|------------|-------|
| **Tertiary** | `#E07A5F` | `#C45B3E` | Highlights, alerts, notifications |
| **On Tertiary** | `#FFFFFF` | `#FFFFFF` | Text on tertiary |
| **Tertiary Container** | `#9C3D29` | `#FCE8E4` | Tertiary containers |
| **On Tertiary Container** | `#FFF1E3` | `#7A2E1A` | Text on tertiary containers |

### Error
| Token | Dark Mode | Light Mode | Usage |
|-------|-----------|------------|-------|
| **Error** | `#FF5252` | `#D32F2F` | Error states |
| **On Error** | `#FFFFFF` | `#FFFFFF` | Text on error |
| **Error Container** | `#CF6679` | `#FFCDD2` | Error containers |
| **On Error Container** | `#B71C1C` | `#9A0007` | Text on error containers |

### Surface (Backgrounds)
| Token | Dark Mode | Light Mode | Usage |
|-------|-----------|------------|-------|
| **Background** | `#121212` | `#FAFAFA` | Page backgrounds |
| **Surface** | `#1E1E1E` | `#FFFFFF` | Cards, sheets |
| **Surface Variant** | `#2A2A2A` | `#F1F3F4` | Dividers, secondary surfaces |
| **On Background** | `#E0E0E0` | `#1A1A1A` | Primary text |
| **On Surface** | `#E3E3E3` | `#1A1A1A` | Text on surface |
| **On Surface Variant** | `#A0A0A0` | `#4A5568` | Secondary text |

### Outline
| Token | Dark Mode | Light Mode | Usage |
|-------|-----------|------------|-------|
| **Outline** | `#3A3A3A` | `#CBD5E0` | Borders, dividers |
| **Outline Variant** | `#4A5A4A` | `#A0AEC0` | Subtle borders |

## Typography

**Font Family:** Montserrat (bundled)

| Style | Weight | Size | Usage |
|-------|--------|------|-------|
| Display Large | Bold | 57sp | - |
| Display Medium | Bold | 45sp | - |
| Display Small | Bold | 36sp | - |
| Headline Large | Bold | 32sp | Screen titles |
| Headline Medium | SemiBold | 28sp | Section headers |
| Headline Small | SemiBold | 24sp | Card titles |
| Title Large | SemiBold | 22sp | List item titles |
| Title Medium | Medium | 16sp | Subtitles |
| Title Small | Medium | 14sp | Small titles |
| Body Large | Regular | 16sp | Primary body text |
| Body Medium | Regular | 14sp | Secondary body text |
| Body Small | Regular | 12sp | Captions, hints |
| Label Large | Medium | 14sp | Buttons |
| Label Medium | Medium | 12sp | Chips, small labels |
| Label Small | Medium | 11sp | Timestamps |

## Spacing

| Token | Value | Usage |
|-------|-------|-------|
| `xs` | 4dp | Tight spacing |
| `sm` | 8dp | Compact spacing |
| `md` | 16dp | Default spacing |
| `lg` | 24dp | Section spacing |
| `xl` | 32dp | Large gaps |
| `xxl` | 48dp | Screen padding top |

## Elevation

| Level | Usage |
|-------|-------|
| Level 0 | Flat surfaces |
| Level 1 | Cards, elevated components |
| Level 2 | Floating action buttons |
| Level 3 | Modals, dialogs |

## Components

### Buttons
- **Primary:** Filled with primary color, rounded corners (12dp)
- **Secondary:** Outlined with secondary color
- **Text:** No background, primary color text
- States: default, pressed (opacity 0.8), disabled (opacity 0.5), loading

### Cards
- Background: Surface color
- Corner radius: 16dp
- Padding: 16dp
- Elevation: Level 1
- Border: 1dp outline variant (optional)

### Inputs
- Height: 56dp
- Corner radius: 12dp
- Border: 1dp outline
- Focus: Primary color border (2dp)
- Error: Error color border

### Navigation
- Bottom nav: 80dp height, surface container background
- Top app bar: 64dp height, transparent/surface
- FAB: 56dp, primary container, level 2 elevation

## Animations

- **Duration:** 200-300ms for most transitions
- **Easing:** Standard (Material Easing)
- **Reduced motion:** Respect `prefers-reduced-motion`

## Accessibility

- Minimum touch target: 48dp
- Text contrast: 4.5:1 minimum (7:1 for primary text)
- Focus indicators visible
- Content descriptions for icons

## Anti-Patterns

- Do NOT use emoji as icons
- Do NOT use scale transforms for hover (causes layout shift)
- Do NOT use `bg-white/10` in light mode (invisible)
- Do NOT use gray-400 for body text in light mode
- Do NOT stick navbar directly to edges without spacing
