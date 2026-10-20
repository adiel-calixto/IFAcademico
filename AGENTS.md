# Repository guide

## Project and layout

IFAcademico is an independent Android client for Q-Acadêmico, currently targeting
IFCE. It uses Kotlin, Jetpack Compose, Material 3, Hilt/KSP, coroutines/Flow,
Retrofit/OkHttp, and Preferences DataStore.

- `app/` is the only Gradle module. Kotlin sources live under
  `app/src/main/java/com/adielcalixto/ifacademico/`.
- `domain/`: entities, error types, the custom `Result`, and use-case interfaces.
- `data/remote/`: Retrofit APIs, DTOs with `toDomain()` mappings, interceptors,
  and GitHub release checking.
- `data/local/`: encrypted session persistence, settings, and an in-memory LRU cache.
- `data/usecases/`: implementations of the domain use-case interfaces.
- `di/`: Hilt providers in `AppModule` and interface bindings in `UseCasesModule`.
- `presentation/`: feature screens, view models, state/action types, shared UI,
  theme configuration at its root, shared `components/`, and feature-owned UI.
- `observers/`: unauthorized-response notifications used by session handling.
- `MainActivity.kt`: app composition, typed `Screen` routes, and navigation wiring.
- `DESIGN.md`: visual specification. `design-system/README.md` explains the native
  implementation and migration status; `design-system/` is documentation, not a
  Gradle module.

## Setup and commands

Run commands from the repository root with the checked-in Gradle wrapper.
The wrapper uses Gradle 9.7.0; daemon JVM criteria request Java 21. The Android
configuration uses compile SDK 37, target SDK 36, and minimum SDK 33.
Java and Kotlin bytecode targets are both 11, as required by Compose inline APIs.
Configure the local Android SDK via `local.properties` or the environment;
`local.properties` is machine-specific and ignored by Git.

```sh
./gradlew :app:assembleDebug                 # Build a debug APK
./gradlew :app:testDebugUnitTest             # JVM unit tests
./gradlew :app:lintDebug                     # Android lint
./gradlew :app:assembleDebugAndroidTest      # Compile instrumentation tests
./gradlew :app:connectedDebugAndroidTest     # Run on a dedicated API 33+ device/emulator
./gradlew :app:installDebug                  # Install on a connected device/emulator
./gradlew :app:assembleRelease              # Build the release variant
```

For a focused unit-test run:

```sh
./gradlew :app:testDebugUnitTest --tests 'com.adielcalixto.ifacademico.presentation.ThemeViewModelTest'
```

Launch the installed app from the device or use Android Studio's app run
configuration. Release builds enable R8 minification and resource shrinking;
review `app/proguard-rules.pro` when changing reflection or serialization behavior.
No automated publishing or release-signing workflow is configured in the build files.

## Architecture and data flow

- Keep business-facing entities and use-case contracts in `domain`; implement
  network/persistence work in `data`. Bind new use-case interfaces in Hilt.
- Feature view models use `@HiltViewModel`, constructor injection, and
  `viewModelScope`. Follow the existing private `MutableStateFlow` / public
  `asStateFlow()` pattern and update state with `copy`. Features commonly pair
  `FooScreen`, `FooViewModel`, `FooState`, and sealed `FooAction` types.
- Screens render state and send actions/callbacks. Wire navigation at the host;
  routes are Kotlin-serialization `@Serializable` types in `Screen`.
- Use the project's `domain.Result.Success` / `Result.Error` and domain error
  hierarchy for use-case results. Convert errors to localized UI text through
  `asUiText` / `UiText` at the presentation boundary.
- Retrofit uses Gson for HTTP payloads. Cached DTOs and session data also use
  kotlinx.serialization; preserve both annotation schemes where applicable.
- Cookie interceptors persist and attach session cookies. HTTP 401 responses
  notify `UnauthorizedApiErrorObserver` and clear the cache; `MainViewModel`
  participates in session verification/refresh and lifecycle handling.
- Session data includes credentials and cookies and is encrypted through
  `EncryptionUtil` before DataStore persistence. Preserve this storage boundary.
  The LRU cache is process-local, stores JSON strings, and returns `""` on a miss.

## UI conventions and gotchas

- Read `DESIGN.md` and `design-system/README.md` before visual changes. The app
  has adopted the foundation and shared controls; academic catalog components
  coexist with older feature layouts during the page redesign.
- Use `AppTheme` at the app boundary. Use the pure `IFTheme(darkTheme = ...)`
  in previews and isolated component tests; it needs neither an Activity nor Hilt.
- Use semantic `IFTheme.colors`, `typography`, `spacing`, and `shapes`, plus
  `IFSize` and `IFMotion`. Layout dimensions use dp and text uses sp. Host pages
  own scrolling and safe-area padding.
- Design-system components accept display records and callbacks. Keep view
  models, API access, and navigation controllers out of these components.
- Callers format dates/numbers and supply calendar markers and attendance
  warnings. Do not infer missing academic data: an absent grade differs from
  zero, and an absent date must remain absent.
- Preserve accessibility semantics, icon action descriptions, touch targets,
  enlarged-text behavior, and sheet dismissal/focus return.
- Appearance is System / Light / Dark. Existing Purple preferences migrate to
  Dark; Yellow/Blue migrate to Light. Unknown values resolve to System. Preserve
  update-check preferences; checking for a release does not install it.
- Default string resources are Brazilian Portuguese (`values/strings.xml`,
  `unqualifiedResLocale=pt-BR`); English translations are in `values-en/strings.xml`.
  Add user-visible copy to resources and keep translations aligned.
- Manrope is bundled locally; retain its OFL license in `assets/licenses`.

## Style, documentation, and logging

- Follow official Kotlin style (`kotlin.code.style=official`): four-space
  indentation, PascalCase types/composables, camelCase functions/properties, and
  the existing package/file organization. Use Android Studio formatting for
  touched code; no standalone formatter task is configured.
- Keep modules loosely coupled and responsibilities focused. Prefer the smallest
  implementation that satisfies the requirement and existing abstractions.
- Comment non-obvious behavior, protocol quirks, or reasoning. Avoid comments
  that repeat code. Update design documentation when changing its contracts.
- An injectable `data.Logger` is backed by `AndroidLogger` and used by the update
  checker; older use cases also use `printStackTrace()`. Prefer the injectable
  logger for new testable logging. HTTP clients currently log at BASIC level.
  Do not log passwords, session cookies, or sensitive academic payloads.
- Always use Conventional Commits, for example `feat: add ...`, `fix: handle ...`,
  or `docs: update ...`.

## Verification

- JVM tests live in `app/src/test/java/` and use JUnit 4, MockK, and
  kotlinx-coroutines-test. Existing coverage includes update checking, theme
  migration/persistence, view-model observation, and text contrast.
- Use `runTest` for coroutine tests and replace/reset `Dispatchers.Main` when
  exercising view models. Use fakes/mocks and local data instead of real accounts.
- Compose instrumentation tests live in `app/src/androidTest/java/`. They cover
  catalog rendering and interactions including selected/disabled semantics,
  modal dismissal/focus, and confirmation cancellation.
- Feature and shared component previews provide light, dark, and narrow
  enlarged-text variants through `IFCatalogPreviews`. Review relevant previews
  when changing shared UI.
- Choose checks appropriate to the change. For shared UI/theme work, the existing
  documented check set is `:app:assembleDebug :app:testDebugUnitTest :app:lintDebug
  :app:assembleDebugAndroidTest`; run connected tests when a suitable test device
  is available. Report any checks that could not run and the reason.
