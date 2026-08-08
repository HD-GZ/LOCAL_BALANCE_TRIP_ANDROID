package live.lb_trip.feature.signin

sealed interface PasswordResetIntent {

    sealed interface EmailStep : PasswordResetIntent
    sealed interface VerifyStep : PasswordResetIntent
    sealed interface NewPasswordStep : PasswordResetIntent

    data object NavigateToSigninClicked : PasswordResetIntent
    data object ErrorMessageConsumed : PasswordResetIntent

    data class EmailChanged(val value: String) : EmailStep
    data object SendCodeClicked : EmailStep

    data class CodeChanged(val value: String) : VerifyStep
    data object ResendCodeClicked : VerifyStep
    data object ConfirmCodeClicked : VerifyStep
    data object VerifyStepLeft : VerifyStep

    data class NewPasswordChanged(val value: String) : NewPasswordStep
    data object ToggleNewPasswordVisibility : NewPasswordStep
    data class NewPasswordConfirmChanged(val value: String) : NewPasswordStep
    data object ToggleNewPasswordConfirmVisibility : NewPasswordStep
    data object ResetPasswordClicked : NewPasswordStep
}
