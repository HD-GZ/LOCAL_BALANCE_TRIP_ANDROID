# PR2 integrated gate receipt

Validated source: `81773b944da3e1b4696dfce793747fa94d7aa673`

Command:

```text
./gradlew ktlintCheck detekt lintDebug assembleDebug --no-daemon --console=plain
```

Result: `BUILD SUCCESSFUL in 34s`; 1038 actionable tasks (21 executed, 1017 up-to-date).

Additional checks:

- `git show --check HEAD`: PASS
- Debug APK SHA-256: `31a26cec0ede736b488fa15d4a8e16533a02797d52fef0b415f36a8bf4b1c2c1`
- Commit subjects from `origin/develop` to the validated source use semantic prefixes; the string-resource commit is `refactor: 문자열 리소스 기반 화면 문구 통일`.

No production source or dependency changes were made after this gate. The gate output contained no credentials, tokens, device identifiers, or network endpoints and was not retained as a raw log.
