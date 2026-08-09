# Post-commit verification receipt

Verified commit: `13d61d5542ace0dcaef34cf6fce122b0b0c211de`.

## Direct checks

- Invocation: `git status --porcelain`
  - Observable: no output; the worktree was clean before this receipt was created.
- Invocation: `git show --check --stat 13d61d5542ace0dcaef34cf6fce122b0b0c211de`
  - Observable: exited successfully with no whitespace-error output; commit subject is `refactor: MainViewModel MVI 패턴 적용`.
- Invocation: non-empty checks for baseline, implementation, gates, manual-QA, and cleanup records.
  - Observable: `EVIDENCE_NONEMPTY_PASS`.
- Invocation: `test -s app/build/outputs/apk/debug/app-debug.apk && wc -c < app/build/outputs/apk/debug/app-debug.apk`
  - Observable: debug APK exists and is 140,543,700 bytes.

Conclusion: the committed scoped MVI implementation, evidence bundle, and compiled Android artifact are present. This receipt is committed with the atomic implementation change.
