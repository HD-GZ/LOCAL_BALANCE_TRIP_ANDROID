# PR2 format gates

- Baseline scenario: `./gradlew ktlintCheck --no-daemon --rerun-tasks` at `7235b1b`; exit 0, `BUILD SUCCESSFUL`, zero findings. Artifact: `baseline.md`.
- Formatting scenario: `./gradlew ktlintCheck --no-daemon --rerun-tasks` after edits; exit 0, all module ktlint checks successful. Observable: Gradle `BUILD SUCCESSFUL`. Artifact: this file and Gradle report files under module `build/reports/ktlint/`.
- Compile scenario: `./gradlew :app:compileDebugKotlin --no-daemon`; exit 0, 214 actionable tasks up-to-date; all dependent modules compile successfully. Observable: Gradle `BUILD SUCCESSFUL` and `:app:compileDebugKotlin UP-TO-DATE`.
- Cleanup scenario: `git diff --check`; exit 0 with no output. Observable: no whitespace errors.
- Source-scope scenario: `git diff --name-only` lists only Kotlin source files plus this evidence directory; `git diff --stat` contains token-only Kotlin changes. No resource/dependency files are present.
- Manual QA handoff (parent-owned, no device touched here): `adb shell am force-stop <redacted-package>`; `adb shell am start -n <redacted-package>/.MainActivity`; `android layout --device <redacted> --pretty`. PASS requires cold boot on expected route with unchanged text/UI. `curl -i`, `send-keys`, and browser page click are N/A for Android.
