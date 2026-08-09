# Parent-owned Android visual QA plan

Do not run until the commit is cherry-picked into the shared-device integration worktree.

1. Launch with `adb shell am start -n <redacted-package>/.MainActivity`.
2. Capture `android layout --device <redacted> --pretty` on onboarding, signup account/personal/verification/completion, signin, password-reset email/verification/new-password/completion, and home.
3. Compare every visible Korean text node to `baseline-matrix.md`, including newlines, spaces in annotated text, and password icon content descriptions.
4. PASS only on exact text equality. FAIL on any difference; record an integration evidence artifact rather than changing this worker commit.

N/A: `curl -i`, `send-keys`, and `browser:control-in-app-browser page.click` do not target this Android application.
