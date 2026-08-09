# PR2 physical QA report

Overall verdict: PASS. The login-only branch and malformed-input probe were not applicable because the preserved authenticated session routed directly to the main app.

## Build and execution identity

- Installed APK: `app/build/outputs/apk/debug/app-debug.apk` using the requested `adb install -r` invocation.
- SHA at install/capture start: `ee74536d7fdcced4e1b66bfe4a3ec27141ac49ef`.
- Final worktree HEAD: `0ce360e9484c13fe3a9e8b606b6e176763afad5b`.
- The two commits after the installed SHA contain evidence-only files; no production source or APK inputs changed during this QA run.
- `git show --check --oneline -1`: clean (`0ce360e chore: TermsScreen 검증 증거 기록`).
- Worktree before: branch `refactor/convention-alignment`, one pre-existing untracked `.omo/evidence/pr2-format-recheck.md`.
- Worktree after: same pre-existing file plus this QA evidence directory; no production-file changes.

## Surface evidence

| Scenario | Criterion | Surface and exact invocation | Verdict | Artifact refs |
|---|---|---|---|---|
| S1 | cold authenticated routing | `adb install -r app/build/outputs/apk/debug/app-debug.apk`; `adb shell am force-stop live.lb_trip.localbalancetrip`; `adb shell monkey -p live.lb_trip.localbalancetrip 1`; visual inspection of the rendered home | PASS — authenticated home rendered with `메인`, saved-course cards, and profile tab; no login shell appeared | A1, A2 |
| S2 | authenticated session/login branch | Same cold launch invocation; login branch was not shown, so no credentials were entered. | PASS — existing authenticated session was preserved; login was not applicable | A1 |
| S3 | Terms route and content parity | From home, inspect profile bounds `[550,2083][1080,2281]`; tap `(815,2180)`; inspect Terms row bounds `[50,1254][1030,1398]`; tap `(540,1326)`; dump UI and screenshot | PASS — UI tree exposed title node bounds `[50,278][464,347]` and article content node `[50,392][1030,575]`; screenshot shows `서비스 이용약관`, 시행일/버전, 제1조–제4조, and temporary-use notice | A3 |
| S4 | offline error snackbar + retry | `adb shell svc wifi disable`; force-stop/monkey launch; navigate using inspected profile/Terms bounds above; wait for load; dump UI and screenshot | PASS — screenshot shows `약관을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.` and visible `재시도`; snackbar bounds `[30,2083][1050,2251]`, retry action bounds `[857,2107][1030,2226]` | A4 |
| S5 | connectivity restore + retry | `adb shell svc wifi enable`; wait; tap inspected retry center `(943,2166)`; dump UI and screenshot | PASS — Terms title/articles returned and snackbar disappeared | A5 |
| S6 | onIntent callback/navigation action | On the Terms surface, tap back bounds `[10,94][129,213]`, then profile’s inspected main-tab bounds `(265,2180)`; screenshot final home | PASS — retry callback and back/navigation actions remained functional; authenticated home rendered after returning | A6, A5 |
| S7 | stringResource visible-text parity | Visual inspection of fresh Terms screenshots and UI-tree article nodes after online load/retry | PASS — Korean title, date/version, four article headings/body copy, and notice were visible without tofu or clipping | A3, A5, A7 |
| S8 | background/resume and rotation | `adb shell input keyevent 3`; `adb shell am start -n live.lb_trip.localbalancetrip/.MainActivity`; screenshot; set `accelerometer_rotation=0`, `user_rotation=1`, screenshot; restore `user_rotation=0`, `accelerometer_rotation=1` | PASS — home remained rendered after background/resume and portrait/landscape rotation; no crash observed; orientation restored | A8, A9 |
| S9 | repeated launch / flaky retry | Force-stop/monkey launch again; inspect profile and Terms bounds before each tap; Terms screenshot after second online load | PASS — second Terms load reproduced the same title/articles | A7 |
| S10 | recommendation/onIntent surface | Inspect saved-course card bounds `[50,1119][785,1649]`; tap `(400,1380)`; visually inspect detail route | PASS — recommendation detail route rendered with course tabs, stop list, and navigation affordance | A10 |

## Adversarial cases

| Scenario | Criterion | Adversarial class | Expected behavior | Verdict | Artifact refs |
|---|---|---|---|---|---|
| ADV1 | session/routing | stale state | Force-stop/relaunch must preserve authentication and remain usable without clearing data | PASS — relaunch showed authenticated home/loading state and no session loss | A11 |
| ADV2 | Terms load | malformed input | No malformed-input branch unless login validation is intentionally encountered | not_applicable — login screen did not appear during authorized session QA |  |
| ADV3 | all surfaces | prompt injection | No external instructions should be accepted as app actions | not_applicable — no external instruction surface was encountered |  |
| ADV4 | Terms navigation | cancel/resume | Background and return must preserve a usable route | PASS — home remained usable after HOME/start resume | A8 |
| ADV5 | command reliability | hung/long command | ADB actions must complete within bounded timeout and have UI corroboration | PASS — all adb actions were timeout-bounded and corroborated by screenshots/UI inspection | A1, A3, A4, A5 |
| ADV6 | result integrity | misleading success output | Exit success alone is insufficient; rendered state must match | PASS — install/launch/retry results were accepted only with matching UI screenshots | A1, A4, A5 |
| ADV7 | state transition | repeated interruption | Rotation/background during load must not crash or lose session | PASS — rotated/resumed home rendered and app process was later force-stopped cleanly | A8, A9 |
| ADV8 | worktree hygiene | dirty worktree | Preserve unrelated changes and report before/after status | PASS — only evidence files were added; pre-existing untracked file was preserved | A12 |

## Artifact references

| ID | Kind | Description | Path |
|---|---|---|---|
| A1 | PNG | authenticated home after cold launch | `home-authenticated.png` |
| A2 | PNG | authenticated home after background/resume | `home-background-resume.png` |
| A3 | PNG | Terms content online | `terms-online.png` |
| A4 | PNG | Terms offline error snackbar with retry | `terms-offline-error.png` |
| A5 | PNG | Terms content after connectivity restore and retry | `terms-online-retry.png` |
| A6 | PNG | home after Terms back/navigation | `home-after-terms-back.png` |
| A7 | PNG | repeated Terms online load | `terms-repeat-online.png` |
| A8 | PNG | home after background/resume | `home-background-resume.png` |
| A9 | PNG | home after landscape rotation | `home-rotation-landscape.png` |
| A10 | PNG | recommendation/saved-course detail route | `recommendation-detail.png` |
| A11 | PNG | authenticated home immediately after force-stop/relaunch | `home-relaunch-authenticated.png` |
| A12 | manifest | checksums and retained safe captures | `checksums.txt` |

All retained PNGs were visually inspected and contain no account identifier, credential, device identifier, or XML. Raw screenshots/XML were removed after inspection.
