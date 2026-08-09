# Implementation

Scenario: startup token observation uses the application MVI boundary.

Change: `MainIntent` now declares `TokensChanged(isLoggedIn)` and `SessionValidationRequested`. The token collector dispatches `TokensChanged`; `onIntent` exhaustively dispatches both cases to private handlers. The first token emission preserves the legacy startup rule: it updates state, then a logged-in value dispatches exactly one validation request; a logged-out value updates state to `false` without validation. Later emissions continue to update state but cannot duplicate startup validation. The initial state remains `null` for splash rendering.

Session behavior: the validation handler preserves the existing `401` condition and emits `MainSideEffect.ShowSessionExpired` once per failed validation result. UI routing, navigation, strings, and dependencies were not changed.

Source-characterization invocation: `rg -n '^sealed interface MainIntent \\{|data class TokensChanged\\(val isLoggedIn: Boolean\\) : MainIntent|data object SessionValidationRequested : MainIntent|collect \\{ isLoggedIn -> onIntent\\(MainIntent\\.TokensChanged\\(isLoggedIn\\)\\) \\}|override fun onIntent\\(intent: MainIntent\\) \\{|when \\(intent\\)|is MainIntent\\.TokensChanged -> handleTokensChanged\\(intent\\)|MainIntent\\.SessionValidationRequested -> validateSession\\(\\)|if \\(intent\\.isLoggedIn\\) onIntent\\(MainIntent\\.SessionValidationRequested\\)|postSideEffect\\(MainSideEffect\\.ShowSessionExpired\\)' app/src/main/java/live/lb_trip/localbalancetrip/MainViewModel.kt`

Binary observable: exit code `0`; all required paths were present. The complementary legacy-pattern search exited `1`, confirming the empty intent, `Unit` handler, and init bypass are absent.

Artifact: this file.
