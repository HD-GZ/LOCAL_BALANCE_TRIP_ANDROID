# PR2 integrated gate receipt

Validated source: `88bd70151929a573ef84f2dab2d381b2d6230cf3`

Command:

```text
./gradlew ktlintCheck detekt lintDebug assembleDebug test --no-daemon --console=plain
```

Result: `BUILD SUCCESSFUL in 3m 9s`; 1106 actionable tasks (738 executed, 368 up-to-date).

Additional checks:

- `git show --check HEAD`: PASS
- Focused regression test `MainViewModelTest.logoutCancelsInFlightSessionValidation`: PASS; it covers cancellation of an in-flight profile check after logout.
- Commit subjects from `origin/develop` to the validated source use semantic prefixes; the string-resource commit is `refactor: 문자열 리소스 기반 화면 문구 통일`.

The gate output contained no credentials, tokens, device identifiers, or network endpoints and was not retained as a raw log.
