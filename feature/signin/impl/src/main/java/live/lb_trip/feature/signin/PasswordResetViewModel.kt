package live.lb_trip.feature.signin

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.auth.LbTripAuthException
import live.lb_trip.domain.usecase.ConfirmPasswordResetUseCase
import live.lb_trip.domain.usecase.RequestPasswordResetUseCase
import live.lb_trip.domain.usecase.ResetPasswordUseCase

@HiltViewModel
class PasswordResetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase,
    private val confirmPasswordResetUseCase: ConfirmPasswordResetUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : BaseViewModel<PasswordResetUiState, PasswordResetIntent, PasswordResetSideEffect>(PasswordResetUiState()) {

    private var timerJob: Job? = null

    override fun onIntent(intent: PasswordResetIntent) {
        when (intent) {
            is PasswordResetIntent.EmailStep -> handleEmailStep(intent)
            is PasswordResetIntent.VerifyStep -> handleVerifyStep(intent)
            is PasswordResetIntent.NewPasswordStep -> handleNewPasswordStep(intent)
            PasswordResetIntent.NavigateToSigninClicked -> postSideEffect(PasswordResetSideEffect.NavigateToSignin)
            PasswordResetIntent.ErrorMessageConsumed -> updateState { it.copy(errorMessage = null) }
        }
    }

    private fun handleEmailStep(intent: PasswordResetIntent.EmailStep) {
        when (intent) {
            is PasswordResetIntent.EmailChanged -> updateState { it.copy(email = intent.value) }
            PasswordResetIntent.SendCodeClicked -> sendCode(isResend = false)
        }
    }

    private fun handleVerifyStep(intent: PasswordResetIntent.VerifyStep) {
        when (intent) {
            is PasswordResetIntent.CodeChanged -> updateState { it.copy(code = intent.value) }
            PasswordResetIntent.ResendCodeClicked -> sendCode(isResend = true)
            PasswordResetIntent.ConfirmCodeClicked -> confirmCode()
            PasswordResetIntent.VerifyStepLeft -> timerJob?.cancel()
        }
    }

    private fun handleNewPasswordStep(intent: PasswordResetIntent.NewPasswordStep) {
        when (intent) {
            is PasswordResetIntent.NewPasswordChanged -> updateState { it.copy(newPassword = intent.value) }
            PasswordResetIntent.ToggleNewPasswordVisibility ->
                updateState { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
            is PasswordResetIntent.NewPasswordConfirmChanged -> updateState { it.copy(newPasswordConfirm = intent.value) }
            PasswordResetIntent.ToggleNewPasswordConfirmVisibility ->
                updateState { it.copy(isNewPasswordConfirmVisible = !it.isNewPasswordConfirmVisible) }
            PasswordResetIntent.ResetPasswordClicked -> resetPassword()
        }
    }

    private fun sendCode(isResend: Boolean) {
        viewModelScope.launch {
            val email = currentState.email
            updateState { it.copy(isLoading = true, errorMessage = null) }
            requestPasswordResetUseCase(email = email)
                .onSuccess { issued ->
                    updateState { it.copy(code = "", remainingSeconds = issued.verificationCodeExpiresIn.toInt()) }
                    startTimer()
                    if (isResend) {
                        postSideEffect(PasswordResetSideEffect.ShowCodeResent)
                    } else {
                        postSideEffect(PasswordResetSideEffect.NavigateToVerify)
                    }
                }
                .onFailure { throwable -> updateState { it.copy(errorMessage = requestFailureMessage(context, throwable)) } }
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun confirmCode() {
        viewModelScope.launch {
            val email = currentState.email
            val code = currentState.code
            updateState { it.copy(isLoading = true, errorMessage = null) }
            confirmPasswordResetUseCase(email = email, code = code)
                .onSuccess { token ->
                    timerJob?.cancel()
                    updateState { it.copy(resetToken = token.resetToken) }
                    postSideEffect(PasswordResetSideEffect.NavigateToNewPassword)
                }
                .onFailure { throwable -> updateState { it.copy(errorMessage = confirmFailureMessage(context, throwable)) } }
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun resetPassword() {
        viewModelScope.launch {
            val current = currentState
            updateState { it.copy(isLoading = true, errorMessage = null) }
            resetPasswordUseCase(resetToken = current.resetToken, newPassword = current.newPassword)
                .onSuccess { postSideEffect(PasswordResetSideEffect.NavigateToComplete) }
                .onFailure { throwable -> updateState { it.copy(errorMessage = resetFailureMessage(context, throwable)) } }
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (currentState.remainingSeconds > 0) {
                delay(1_000)
                updateState { it.copy(remainingSeconds = (it.remainingSeconds - 1).coerceAtLeast(0)) }
            }
        }
    }
}

private fun requestFailureMessage(context: Context, throwable: Throwable): String = when (throwable) {
    is LbTripAuthException.InvalidInputValueException -> context.getString(R.string.password_reset_error_invalid_email)
    is LbTripAuthException.UserNotFoundException -> context.getString(R.string.password_reset_error_user_not_found)
    is LbTripAuthException.UserWithdrawnException -> context.getString(R.string.password_reset_error_user_withdrawn)
    is LbTripAuthException.EmailNotVerifiedException -> context.getString(R.string.password_reset_error_email_not_verified)
    else -> context.getString(R.string.password_reset_error_request_failed)
}

private fun confirmFailureMessage(context: Context, throwable: Throwable): String = when (throwable) {
    is LbTripAuthException.PasswordResetCodeExpiredException -> context.getString(R.string.password_reset_error_code_expired)
    is LbTripAuthException.PasswordResetCodeUsedException -> context.getString(R.string.password_reset_error_code_used)
    is LbTripAuthException.PasswordResetCodeNotFoundException -> context.getString(R.string.password_reset_error_code_invalid)
    is LbTripAuthException.UserNotFoundException -> context.getString(R.string.password_reset_error_user_not_found)
    is LbTripAuthException.UserWithdrawnException -> context.getString(R.string.password_reset_error_user_withdrawn)
    is LbTripAuthException.EmailNotVerifiedException -> context.getString(R.string.password_reset_error_email_verification_needed)
    else -> context.getString(R.string.password_reset_error_verification_failed)
}

private fun resetFailureMessage(context: Context, throwable: Throwable): String = when (throwable) {
    is LbTripAuthException.PasswordResetTokenExpiredException -> context.getString(R.string.password_reset_error_token_expired)
    is LbTripAuthException.PasswordResetTokenUsedException -> context.getString(R.string.password_reset_error_token_used)
    is LbTripAuthException.PasswordResetTokenNotFoundException -> context.getString(R.string.password_reset_error_token_not_found)
    is LbTripAuthException.UserWithdrawnException -> context.getString(R.string.password_reset_error_user_withdrawn)
    is LbTripAuthException.InvalidInputValueException -> context.getString(R.string.password_reset_error_invalid_password)
    else -> context.getString(R.string.password_reset_error_reset_failed)
}
