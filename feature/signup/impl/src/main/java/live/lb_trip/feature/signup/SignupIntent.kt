package live.lb_trip.feature.signup

import live.lb_trip.domain.model.Gender

sealed interface SignupIntent {

    sealed interface AccountInfo : SignupIntent
    sealed interface PersonalInfo : SignupIntent
    sealed interface EmailVerify : SignupIntent

    data object NavigateToSigninClicked : SignupIntent

    data class EmailChanged(val value: String) : AccountInfo
    data class PasswordChanged(val value: String) : AccountInfo
    data object TogglePasswordVisibility : AccountInfo
    data class PasswordConfirmChanged(val value: String) : AccountInfo
    data object ToggleConfirmPasswordVisibility : AccountInfo
    data object AccountInfoNextStepClicked : AccountInfo

    data class NameChanged(val value: String) : PersonalInfo
    data class BirthYearChanged(val value: String) : PersonalInfo
    data class BirthMonthChanged(val value: Int) : PersonalInfo
    data class BirthDayChanged(val value: String) : PersonalInfo
    data class GenderChanged(val value: Gender) : PersonalInfo
    data object ToggleTos : PersonalInfo
    data object TogglePrivacy : PersonalInfo
    data object ToggleMarketing : PersonalInfo
    data object ToggleAllTerms : PersonalInfo
    data object PersonalInfoNextStepClicked : PersonalInfo

    data class CodeChanged(val value: String) : EmailVerify
    data object ResendCodeClicked : EmailVerify
    data object ConfirmCodeClicked : EmailVerify
    data object EmailVerifyStepLeft : EmailVerify
}
