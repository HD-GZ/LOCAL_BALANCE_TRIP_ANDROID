# Gate record

## Targeted feature compile

- Scenario: compile all changed Android feature implementations after resource migration.
- Invocation: `./gradlew :feature:onboarding:impl:compileDebugKotlin :feature:signup:impl:compileDebugKotlin :feature:signin:impl:compileDebugKotlin --console=plain`
- Binary observable: `BUILD SUCCESSFUL in 13s`; all three `compileDebugKotlin` tasks `UP-TO-DATE` after the initial successful compile.

## Static scope gate

- Scenario: no Korean literals remain in the six assigned sources.
- Invocation: `rg -n --glob '*.kt' '"[^"\\n]*[가-힣][^"\\n]*"' feature/{signup,signin,onboarding,home}/impl/src/main/java/live/lb_trip/feature/{signup,signin,onboarding,home}`
- Binary observable: assigned files return no matches; only explicitly out-of-scope SigninViewModel, SigninNavigation, and Signup preview literals remain.

## Diff whitespace gate

- Invocation: `git diff --check`
- Binary observable: exit 0 with no output.

## Full requested gate

- Invocation: `./gradlew ktlintCheck detekt lintDebug assembleDebug --console=plain`
- Binary observable: `BUILD SUCCESSFUL in 24s`; `1038 actionable tasks: 21 executed, 1017 up-to-date`.
