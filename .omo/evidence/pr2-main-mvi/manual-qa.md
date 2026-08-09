# Parent integration manual-QA plan

Run only after cherry-picking onto the parent worktree’s shared authenticated Android device. Substitute the redacted values supplied by the parent environment; do not record device identifiers, tokens, or raw logs.

## Logged-out cold start

```text
adb shell am force-stop <redacted-package>
adb shell am start -n <redacted-package>/.MainActivity
android layout --device <redacted> --pretty
```

PASS when the splash resolves to onboarding/sign-in and no session-expiry Toast appears. FAIL if the route remains blank, opens the main graph, or emits a duplicate/unexpected Toast.

## Authenticated cold start

```text
adb shell am force-stop <redacted-package>
adb shell am start -n <redacted-package>/.MainActivity
android layout --device <redacted> --pretty
```

PASS when the splash resolves to the main/home graph and no duplicate session-expiry Toast/side effect appears. FAIL otherwise.

## Authenticated expired-session cold start

Use the parent’s existing expired-session fixture, then repeat the authenticated command sequence. PASS when the main branch is reached from persisted tokens and exactly one long session-expiry Toast is visible. FAIL on zero or duplicate Toasts.

`curl -i`, `send-keys`, malformed input, prompt injection, and browser interaction are N/A for this Android activity flow.
