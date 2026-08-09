# Gates

Scenario: Kotlin compilation of the changed application module.

Invocation: `timeout 240s ./gradlew :app:compileDebugKotlin --no-daemon --console=plain`

Binary observable: exit code `0`; terminal summary contained `BUILD SUCCESSFUL`; `:app:compileDebugKotlin` completed successfully.

Scenario: repository static checks requested for the application module.

Invocation: `timeout 300s ./gradlew :app:ktlintCheck :app:detekt --no-daemon --console=plain`

Binary observable: exit code `0`; terminal summary contained `BUILD SUCCESSFUL`; both `:app:ktlintCheck` and `:app:detekt` completed successfully.

Scenario: whitespace validation.

Invocation: `git diff --check`

Binary observable: exit code `0` with no output.

Tests: no test was added or run. No existing non-mirroring seam for this one-file convention repair was identified; the source-characterization proof in `implementation.md` is the applicable verification.

Full integrated gate: not run; this artifact does not claim it.

Artifact: this file. Raw Gradle logs were not retained.
