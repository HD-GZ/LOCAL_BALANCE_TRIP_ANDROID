package live.lb_trip.feature.signin

data class PasswordResetUiState(
    val email: String = "",
    val code: String = "",
    val remainingSeconds: Int = 0,
    val resetToken: String = "",
    val newPassword: String = "",
    val newPasswordConfirm: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isNewPasswordConfirmVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface PasswordResetSideEffect {
    data object NavigateToVerify : PasswordResetSideEffect
    data object NavigateToNewPassword : PasswordResetSideEffect
    data object NavigateToComplete : PasswordResetSideEffect
    data object NavigateToSignin : PasswordResetSideEffect
    data object ShowCodeResent : PasswordResetSideEffect
}
