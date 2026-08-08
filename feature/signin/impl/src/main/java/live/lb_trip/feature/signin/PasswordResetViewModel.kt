package live.lb_trip.feature.signin

import androidx.lifecycle.viewModelScope
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
                .onFailure { throwable -> updateState { it.copy(errorMessage = requestFailureMessage(throwable)) } }
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
                .onFailure { throwable -> updateState { it.copy(errorMessage = confirmFailureMessage(throwable)) } }
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun resetPassword() {
        viewModelScope.launch {
            val current = currentState
            updateState { it.copy(isLoading = true, errorMessage = null) }
            resetPasswordUseCase(resetToken = current.resetToken, newPassword = current.newPassword)
                .onSuccess { postSideEffect(PasswordResetSideEffect.NavigateToComplete) }
                .onFailure { throwable -> updateState { it.copy(errorMessage = resetFailureMessage(throwable)) } }
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

private fun requestFailureMessage(throwable: Throwable): String = when (throwable) {
    is LbTripAuthException.InvalidInputValueException -> "올바른 이메일 형식이 아니에요."
    is LbTripAuthException.UserNotFoundException -> "가입되지 않은 이메일이에요."
    is LbTripAuthException.UserWithdrawnException -> "탈퇴한 계정이에요."
    is LbTripAuthException.EmailNotVerifiedException -> "이메일 인증이 필요해요. 가입 시 받은 인증 메일을 확인해 주세요."
    else -> "인증 코드 발송에 실패했어요. 잠시 후 다시 시도해 주세요."
}

private fun confirmFailureMessage(throwable: Throwable): String = when (throwable) {
    is LbTripAuthException.PasswordResetCodeExpiredException -> "인증 코드가 만료됐어요."
    is LbTripAuthException.PasswordResetCodeUsedException -> "이미 사용된 인증 코드예요."
    is LbTripAuthException.PasswordResetCodeNotFoundException -> "인증 코드가 올바르지 않아요."
    is LbTripAuthException.UserNotFoundException -> "가입되지 않은 이메일이에요."
    is LbTripAuthException.UserWithdrawnException -> "탈퇴한 계정이에요."
    is LbTripAuthException.EmailNotVerifiedException -> "이메일 인증이 필요해요."
    else -> "인증에 실패했어요. 잠시 후 다시 시도해 주세요."
}

private fun resetFailureMessage(throwable: Throwable): String = when (throwable) {
    is LbTripAuthException.PasswordResetTokenExpiredException -> "재설정 시간이 만료됐어요. 처음부터 다시 시도해 주세요."
    is LbTripAuthException.PasswordResetTokenUsedException -> "이미 처리된 요청이에요. 처음부터 다시 시도해 주세요."
    is LbTripAuthException.PasswordResetTokenNotFoundException -> "재설정 요청을 찾을 수 없어요. 처음부터 다시 시도해 주세요."
    is LbTripAuthException.UserWithdrawnException -> "탈퇴한 계정이에요."
    is LbTripAuthException.InvalidInputValueException -> "비밀번호는 영문·숫자 포함 8자 이상이어야 해요."
    else -> "비밀번호 변경에 실패했어요. 잠시 후 다시 시도해 주세요."
}
