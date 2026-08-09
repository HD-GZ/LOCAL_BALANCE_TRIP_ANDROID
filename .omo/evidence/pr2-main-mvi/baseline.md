# PR2 MainViewModel MVI baseline

Baseline commit: `7235b1b0727b97a0ccec60fe412b4fbd9ef848dc` (`origin/develop`).

## Source characterization

- `MainViewModel.isLoggedIn` is an eagerly collected `StateFlow<Boolean?>` whose initial value is `null`; a token emission maps to `true` when non-null and `false` when null.
- `MainActivity` keeps the splash visible while that value is `null`, renders `MainNavGraph` for `true`, and renders `AuthNavGraph` (whose start destination is `OnboardingRoute`) for `false`.
- On the first non-null logged-in value only, `MainViewModel` calls `GetUserProfileUseCase`. An `ApiException` with status `401` sends the buffered `sessionExpiredEvent`; `MainActivity` consumes that event and shows `main_session_expired` as a long Toast.
- Logout is not owned by `MainViewModel`: `SettingsViewModel` clears the session. The token flow therefore drives the MainActivity branch to the logged-out graph.

## Failing-first manual scenarios

No non-mirroring unit-test seam exists in `app`: the only tests are template examples, while this Hilt ViewModel receives concrete use cases and its startup behavior is observed through the activity/splash/navigation boundary. No production code was changed before recording these scenarios.

1. **Logged out, cold start** — with no persisted tokens, force-stop then launch the app. Expected baseline observable: the splash resolves and the onboarding/sign-in flow is visible; no session-expiry Toast is shown.
2. **Logged in, valid session, cold start** — with persisted valid tokens, force-stop then launch. Expected baseline observable: the splash resolves to the main/home graph; no Toast is shown.
3. **Logged in, expired session, cold start** — with persisted expired tokens and a profile response of HTTP 401, force-stop then launch. Expected baseline observable: the main/home branch is initially selected from the token state and exactly one long Toast says `로그인이 만료됐어요. 다시 로그인해 주세요.`.

The authenticated-device execution is intentionally deferred to parent integration because this isolated worktree has no assigned device/session and the task prohibits retaining device state or identifiers.
