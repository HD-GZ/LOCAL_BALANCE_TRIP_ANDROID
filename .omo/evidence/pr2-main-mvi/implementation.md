# Implementation record

## Scoped source changes

- `MainViewModel` now extends `BaseViewModel<MainUiState, MainIntent, MainSideEffect>`.
- `MainUiState.isLoggedIn` preserves the nullable startup state: `null` holds the splash, `true` renders the main graph, and `false` renders onboarding/sign-in.
- Token collection continues to update login state for later login/logout transitions. A separate first non-null state observation preserves the original one-time authenticated startup profile validation.
- HTTP 401 profile validation now posts `MainSideEffect.ShowSessionExpired`; `MainActivity` consumes that side effect and retains the existing long Toast text.
- `app/build.gradle.kts` adds the direct `projects.core` dependency. This is required for Hilt/KSP to recognize the inherited `androidx.lifecycle.ViewModel` superclass; every feature module that already uses `BaseViewModel` has the same direct dependency.

## Static implementation observable

Command:

```text
rg -n "BaseViewModel<MainUiState, MainIntent, MainSideEffect>|MainSideEffect.ShowSessionExpired|uiState.value.isLoggedIn|viewModel.sideEffect" app/src/main/java/live/lb_trip/localbalancetrip/MainViewModel.kt app/src/main/java/live/lb_trip/localbalancetrip/MainActivity.kt
```

Observed required bindings:

- `MainViewModel` uses the requested base type and posts `ShowSessionExpired`.
- `MainActivity` reads `uiState.value.isLoggedIn` for the splash and collects `viewModel.sideEffect` for the Toast.
- A follow-up search found neither `sessionExpiredEvent` nor the old `StateFlow` login stream in the main entry package.
