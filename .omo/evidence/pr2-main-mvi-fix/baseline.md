# Baseline

Scenario: characterize the assigned `MainViewModel` at the required base commit.

Invocation: `rg -n '^sealed interface MainIntent$|override fun onIntent\\(intent: MainIntent\\) = Unit|collect \\{ isLoggedIn -> updateState|filterNotNull\\(\\)\\.first\\(\\)' app/src/main/java/live/lb_trip/localbalancetrip/MainViewModel.kt`

Binary observable: exit code `0`; the source contained an empty `MainIntent`, a `Unit` no-op `onIntent`, token collection that updated state directly, and init-time validation that bypassed `onIntent`.

Artifact: this file. No tokens, device data, or raw command output were recorded.
