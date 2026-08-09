# Implementation record

Migrated scoped hardcoded display and error strings to feature-local `res/values/strings.xml` files. Compose call sites use `stringResource`; `SignupViewModel` and `PasswordResetViewModel` use Hilt application `Context.getString` so their existing `String` error-message contracts and error routing remain unchanged.

Changed production files:

- `feature/onboarding/impl/src/main/res/values/strings.xml`
- `feature/onboarding/impl/src/main/java/live/lb_trip/feature/onboarding/OnboardingScreen.kt`
- `feature/signup/impl/src/main/res/values/strings.xml`
- `feature/signup/impl/src/main/java/live/lb_trip/feature/signup/SignupScreen.kt`
- `feature/signup/impl/src/main/java/live/lb_trip/feature/signup/SignupViewModel.kt`
- `feature/signin/impl/src/main/res/values/strings.xml`
- `feature/signin/impl/src/main/java/live/lb_trip/feature/signin/SigninScreen.kt`
- `feature/signin/impl/src/main/java/live/lb_trip/feature/signin/PasswordResetScreen.kt`
- `feature/signin/impl/src/main/java/live/lb_trip/feature/signin/PasswordResetViewModel.kt`

Static inspection after edit:

`rg -n --glob '*.kt' '"[^"\\n]*[가-힣][^"\\n]*"' feature/{signup,signin,onboarding,home}/impl/src/main/java/live/lb_trip/feature/{signup,signin,onboarding,home}`

Result: no Korean literals remain in the six assigned files. Remaining matches are explicitly out of scope: `SigninViewModel.kt`, `SigninNavigation.kt`, and the `SignupScreen` preview fixture.
