# PR2 format implementation

- Scope: formatting tokens only; no resources, behavior, dependencies, tests, or device state changed.
- `LbButton`: added trailing commas to the final parameter and forwarded argument.
- `` constructors: normalized 21 split declarations to the repository-wide `class X  constructor(` form with matching indentation.
- Imports: normalized affected Kotlin import blocks to ktlint ordering, including placing `java`/`javax`/`kotlin` packages at the end and preserving aliases at the end.
- Equivalent ktlint-only fix: wrapped `CheckEmailAvailabilityUseCase` body expression onto the signature line as required by ktlint.

## Changed files

app/src/androidTest/java/live/lb_trip/localbalancetrip/ExampleInstrumentedTest.kt
core/designsystem/src/main/java/live/lb_trip/core/designsystem/component/LbButton.kt
core/designsystem/src/main/java/live/lb_trip/core/designsystem/component/LbStepIndicator.kt
data/src/main/java/live/lb_trip/data/datasource/remote/AuthRemoteDataSource.kt
data/src/main/java/live/lb_trip/data/datasource/remote/SavedCourseRemoteDataSource.kt
data/src/main/java/live/lb_trip/data/datasource/remote/TermsRemoteDataSource.kt
data/src/main/java/live/lb_trip/data/datasource/remote/UserRemoteDataSource.kt
data/src/main/java/live/lb_trip/data/di/DataModule.kt
data/src/main/java/live/lb_trip/data/repository/SavedCourseRepositoryImpl.kt
data/src/main/java/live/lb_trip/data/repository/TermsRepositoryImpl.kt
domain/src/main/java/live/lb_trip/domain/usecase/CheckEmailAvailabilityUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/ClearSessionUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/ConfirmEmailVerificationUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/ConfirmPasswordResetUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/GetPropensityResultUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/GetTokensUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/GetUserProfileUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/LoginUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/LogoutUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/RequestPasswordResetUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/ResendEmailVerificationUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/ResetPasswordUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/SignupUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/SubmitPropensityUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/UpdateUserProfileUseCase.kt
domain/src/main/java/live/lb_trip/domain/usecase/WithdrawUserUseCase.kt
feature/propensity/impl/src/main/java/live/lb_trip/feature/propensity/PropensityScreen.kt
feature/recommendation/impl/src/main/java/live/lb_trip/feature/recommendation/DetailScreen.kt
feature/savedcourses/impl/src/main/java/live/lb_trip/feature/savedcourses/ReceiptCaptureScreen.kt
feature/savedcourses/impl/src/main/java/live/lb_trip/feature/savedcourses/SavedCourseDetailScreen.kt
feature/savedcourses/impl/src/main/java/live/lb_trip/feature/savedcourses/SavedCoursesScreen.kt
feature/settings/impl/src/main/java/live/lb_trip/feature/settings/EditProfileScreen.kt
feature/settings/impl/src/main/java/live/lb_trip/feature/settings/EditProfileViewModel.kt
feature/signin/impl/src/main/java/live/lb_trip/feature/signin/SigninScreen.kt
feature/signup/impl/src/main/java/live/lb_trip/feature/signup/SignupScreen.kt
feature/tour/impl/src/main/java/live/lb_trip/feature/tour/TourScreen.kt
