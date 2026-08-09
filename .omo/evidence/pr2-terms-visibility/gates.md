## Targeted gates

Scenario: compile, lint, and detekt the settings implementation after the visibility change.
Invocation: `./gradlew :feature:settings:impl:compileDebugKotlin :feature:settings:impl:ktlintCheck :feature:settings:impl:detekt --no-daemon --console=plain`
Exit code: 0.
Binary observable: `BUILD SUCCESSFUL`; all three requested tasks completed.

Scenario: changed-file whitespace diagnostics.
Invocation: `git diff --check -- feature/settings/impl/src/main/java/live/lb_trip/feature/settings/TermsScreen.kt`
Exit code: 0 (clean).

Scenario: declaration visibility static check.
Invocation: `rg -n ^internal fun TermsScreen\( feature/settings/impl/src/main/java/live/lb_trip/feature/settings/TermsScreen.kt`
Exit code: 0 (matching internal declaration found).

Scenario: production diff scope.
Invocation: `git diff --name-only`.
Exit code: 0; expected production path is the only tracked changed production file (evidence files are separate untracked scope).

Captured command output was inspected locally and is not committed; sanitized result is recorded here.
