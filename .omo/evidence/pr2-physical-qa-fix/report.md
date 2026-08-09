# PR2 physical QA report

Date: 2026-08-10 (Asia/Seoul)

Build identity: `f7223ff4de947aee02236d136d64e60f6207369f` (evidence-only descendant; captured APK bytes unchanged)

APK: `app/build/outputs/apk/debug/app-debug.apk`; timestamp `2026-08-10 01:07:36 +0900`; SHA-256 `31a26cec0ede736b488fa15d4a8e16533a02797d52fef0b415f36a8bf4b1c2c1`

Ancestry/worktree: source was not edited in this QA attempt. Worktree was clean before the run; only this evidence directory is untracked afterward.

## Execution summary

The APK install succeeded. A preserved existing session cold-launched directly to the authenticated main/home graph. Visible profile navigation exposed logout; logout returned to the unauthenticated onboarding surface. Login, password-reset, and first-page signup surfaces were captured with resource-backed Korean copy and no submitted account-affecting action. The authorized relogin attempt was bounded and blocked: after exact credential entry, the visible login action produced no route transition or rendered error, so authenticated-only Terms and post-login scenarios could not be run. No raw UI XML, logcat, credentials, tokens, email addresses, or device identifiers are retained.

## Invocation and verdict matrix

### surfaceEvidence

| Scenario | Criterion | Surface | Exact invocation / action | Verdict | Artifact refs |
|---|---|---|---|---|---|
| S1 | MainViewModel state routing | Android app, cold launch with existing session | `adb install -r app/build/outputs/apk/debug/app-debug.apk`; `adb shell am force-stop live.lb_trip.localbalancetrip`; `adb shell monkey -p live.lb_trip.localbalancetrip 1`; `adb shell uiautomator dump /sdcard/window.xml`; `adb exec-out screencap -p > /tmp/pr2-fix-screen.png` | PASS — rendered authenticated home with main navigation | A1 |
| S1 | MainViewModel state routing | Android app, profile logout then relaunch | Visible profile tab; visible `로그아웃`; force-stop and `monkey` relaunch; UI dump and screenshot | PASS — rendered onboarding/sign-in surface after logout and relaunch | A2 |
| S2 | Terms retry | Android app, authenticated profile → Terms | Not run: authorized relogin did not return to authenticated graph, so profile → `이용약관 확인` was unavailable. Intended bounded actions were `adb shell svc wifi disable`, UI dump/screenshot, visible `재시도`, `adb shell svc wifi enable`. | BLOCKED — missing authenticated session prerequisite | A2 |
| S3 | Step 2.4 visible string parity | Android onboarding | UI dump and screenshot after logout/relaunch | PASS — visible onboarding headings/buttons | A2 |
| S3 | Step 2.4 visible string parity | Android sign-in | Visible `이미 계정이 있어요 · 로그인`; UI dump and screenshot | PASS — visible login headings, fields, and buttons | A3 |
| S3 | Step 2.4 visible string parity | Android password reset | Visible `비밀번호 찾기`; UI dump and screenshot | PASS — visible reset step-1 headings and email field | A4 |
| S3 | Step 2.4 visible string parity | Android signup first page | Visible `회원가입`; UI dump and screenshot | PASS — visible first-page signup copy and fields; no submission | A5 |
| S4 | onIntent/retry/navigation | Android Terms retry and Home/Saved/Recommendation actions | Not run: authenticated graph unavailable after bounded relogin block. | BLOCKED — missing authenticated session prerequisite | A2 |
| S5 | stale state | Android authenticated relaunch after up to 8 seconds | Not run in authenticated state: relogin blocked. Unauthenticated `HOME` → explicit `am start -n live.lb_trip.localbalancetrip/.MainActivity` rendered onboarding, but this is not an authenticated stale-state PASS. | BLOCKED — missing authenticated session prerequisite | A2 |
| S5 | stale state | Android rotation | `adb shell settings put system accelerometer_rotation 0`; `adb shell settings put system user_rotation 1`; UI dump/screenshot; restore `user_rotation 0`, `accelerometer_rotation 1` | BLOCKED for authenticated criterion — only unauthenticated onboarding was available; landscape rendered but showed heading/badge overlap | A2 |
| S6 | session-expired 401 | Android authorized existing route | No sanctioned expired-session fixture or naturally expiring authorized route was available; no token/network spoofing performed. | NOT_APPLICABLE — one-line reason: no authorized route naturally yielded a 401 response | A1 |

### adversarialCases

| Scenario | Criterion | Adversarial class | Expected behavior | Verdict | Artifact refs |
|---|---|---|---|---|---|
| A1 | ULTRAQA | malformed input | Validation should render a safe error without account mutation | NOT_APPLICABLE — no malformed input was authorized or encountered; only exact credential entry was attempted | A3 |
| A2 | ULTRAQA | prompt injection | UI copy must remain inert content, not alter QA execution | NOT_APPLICABLE — no prompt/content entry surface was reached | A2 |
| A3 | ULTRAQA | cancel/resume | Background then explicit activity resume should render a usable surface without crash | BLOCKED — resume was exercised on onboarding, but no post-resume screenshot was retained; authenticated prerequisite was also unavailable | A2 |
| A4 | ULTRAQA | dirty worktree before/after | QA must not modify production source | PASS — clean before; only evidence directory untracked after | report.md |
| A5 | ULTRAQA | hung/long command | adb actions remain bounded and complete | PASS — all device commands used timeout wrappers; install completed successfully | report.md |
| A6 | ULTRAQA | flaky retry | Repeating Terms retry should settle to articles and no duplicate action | BLOCKED — Terms route unavailable without authenticated session | A2 |
| A7 | ULTRAQA | misleading success output | Install success must correspond to a rendered launched app | PASS — install exit succeeded and fresh launch rendered onboarding/authenticated home states | A1 |
| A8 | ULTRAQA | repeated interruptions | Rotation/background during load must not leave an unusable state | BLOCKED — authenticated load route unavailable; unauthenticated landscape showed overlap and was restored | A2 |

## Artifact references

| ID | Kind | Description | Path |
|---|---|---|---|
| A1 | PNG | Fresh authenticated main/home screenshot; no secret or account text visible | `01-authenticated-home.png` |
| A2 | PNG | Fresh logged-out onboarding screenshot; visible Korean onboarding and login/signup actions | `02-logged-out-onboarding.png` |
| A3 | PNG | Fresh login screenshot with empty resource-backed placeholders; no submitted data retained | `03-login.png` |
| A4 | PNG | Fresh password-reset step-1 screenshot with empty email placeholder | `04-password-reset.png` |
| A5 | PNG | Fresh signup step-1 screenshot with empty fields; no submission | `05-signup-step1.png` |
| A6 | SHA manifest | Checksums and file-format identity for retained PNGs | `checksums.txt` |
| A7 | Text | Cleanup and device-state receipt | `cleanup.md` |

All retained PNGs are non-empty 1080x2340 RGBA PNGs. Generic email placeholder text was redacted from the retained login/reset/signup images; their headings, labels, buttons, and field surfaces remain visible. Temporary captures and `/sdcard/window.xml` were removed during cleanup.
