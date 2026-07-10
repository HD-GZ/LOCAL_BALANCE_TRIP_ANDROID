package live.lb_trip.feature.signup

import live.lb_trip.domain.model.Gender

enum class SignupStep { AccountInfo, PersonalInfo, EmailVerify, Complete }

data class SignupUiState(
    val step: SignupStep = SignupStep.AccountInfo,
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val name: String = "",
    val birthYear: String = "",
    val birthMonth: Int = 0,
    val birthDay: String = "",
    val gender: Gender = Gender.NOT_SPECIFIED,
    val termsAgreed: Boolean = false,
    val privacyAgreed: Boolean = false,
    val marketingAgreed: Boolean = false,
    val code: String = "",
    val remainingSeconds: Int = 300,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val eventSink: (SignupEvent) -> Unit = {},
)

sealed interface SignupEvent {
    data object NavigateBack : SignupEvent
    data class UpdateEmail(val email: String) : SignupEvent
    data class UpdatePassword(val password: String) : SignupEvent
    data class UpdatePasswordConfirm(val passwordConfirm: String) : SignupEvent
    data object TogglePasswordVisibility : SignupEvent
    data object ToggleConfirmPasswordVisibility : SignupEvent
    data object NextStep : SignupEvent
    data class UpdateName(val name: String) : SignupEvent
    data class UpdateBirthYear(val year: String) : SignupEvent
    data class UpdateBirthMonth(val month: Int) : SignupEvent
    data class UpdateBirthDay(val day: String) : SignupEvent
    data class UpdateGender(val gender: Gender) : SignupEvent
    data object ToggleTos : SignupEvent
    data object TogglePrivacy : SignupEvent
    data object ToggleMarketing : SignupEvent
    data object ToggleAllTerms : SignupEvent
    data class UpdateCode(val code: String) : SignupEvent
    data object ResendCode : SignupEvent
    data object ConfirmCode : SignupEvent
    data object NavigateToSignin : SignupEvent
}

sealed interface SignupEffect {
    data object NavigateBack : SignupEffect
    data object NavigateToSignin : SignupEffect
}
