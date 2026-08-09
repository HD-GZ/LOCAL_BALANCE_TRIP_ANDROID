# PR2 format baseline

- Commit: `7235b1b` (`origin/develop`), branch `refactor/pr2-format`.
- Worktree status before edits: clean (`git status --short --branch` reported only the branch line).
- Source diff baseline: `git diff --stat` and `git diff --check` produced no output; no source changes existed.
- Formatting check invocation: `./gradlew ktlintCheck --no-daemon --rerun-tasks`.
- Formatting check result: exit 0, `BUILD SUCCESSFUL` (72 actionable tasks executed).
- Exact ktlint findings: none; no reporter errors were emitted. The non-rerun check was also exit 0 but up-to-date, so the rerun result is the authoritative baseline.
- Manual source characterization (read-only): `LbButton.kt` lacked trailing commas after the final parameters in both multiline declarations; 18 declarations used the split `class X` / `@Inject` / `constructor(` form; import blocks contained ordering anomalies (including `SignupScreen.kt`'s `SpanStyle`/`buildAnnotatedString` and `KeyboardType` ordering). These are style-only observations; ktlint 1.5.0 did not report them.
- Scope guard: no device, network, credentials, or external service was accessed.
