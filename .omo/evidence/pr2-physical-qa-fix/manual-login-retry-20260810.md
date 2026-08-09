# Manual login retry receipt

Date: 2026-08-10 (Asia/Seoul)

Build identity: `81773b944da3e1b4696dfce793747fa94d7aa673`

APK SHA-256: `31a26cec0ede736b488fa15d4a8e16533a02797d52fef0b415f36a8bf4b1c2c1`

## Scenario

- Started from the unauthenticated sign-in surface on the connected Android device.
- Entered the user-provided test account privately; no credential value was written to this receipt, screenshots, UI dumps, logs, or commits.
- Cleared both fields before entry, dismissed the keyboard, and activated the visible 로그인 action.
- Waited 12 seconds, then inspected only redacted UI labels and route state.

## Observable

Verdict: `BLOCKED`.

The app did not reach the authenticated home graph. The visible state remained in the unauthenticated auth flow/onboarding surface. A bounded logcat probe after the submit contained HTTP status markers `403` and `500`; raw logcat was not retained. This is an external authentication/backend response, not a successful login observation.

## Cleanup

- App force-stopped after the attempt.
- Remote UI XML removed.
- No screenshots, account values, tokens, device identifiers, or raw logs retained from this retry.
