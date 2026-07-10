package live.lb_trip.feature.signup

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.auth.LbTripAuthException
import live.lb_trip.domain.model.Gender
import live.lb_trip.domain.usecase.CheckEmailAvailabilityUseCase
import live.lb_trip.domain.usecase.ConfirmEmailVerificationUseCase
import live.lb_trip.domain.usecase.ResendEmailVerificationUseCase
import live.lb_trip.domain.usecase.SignupUseCase

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val signupUseCase: SignupUseCase,
    private val checkEmailAvailabilityUseCase: CheckEmailAvailabilityUseCase,
    private val confirmEmailVerificationUseCase: ConfirmEmailVerificationUseCase,
    private val resendEmailVerificationUseCase: ResendEmailVerificationUseCase,
) : BaseViewModel<SignupUiState, SignupSideEffect>(SignupUiState()) {

    private var timerJob: Job? = null

    fun navigateBack() {
        when (currentState.step) {
            SignupStep.AccountInfo -> postSideEffect(SignupSideEffect.NavigateBack)
            SignupStep.PersonalInfo -> updateState { it.copy(step = SignupStep.AccountInfo) }
            SignupStep.EmailVerify -> {
                timerJob?.cancel()
                updateState { it.copy(step = SignupStep.PersonalInfo) }
            }
            SignupStep.Complete -> postSideEffect(SignupSideEffect.NavigateBack)
        }
    }

    fun updateEmail(email: String) = updateState { it.copy(email = email) }

    fun updatePassword(password: String) = updateState { it.copy(password = password) }

    fun updatePasswordConfirm(passwordConfirm: String) =
        updateState { it.copy(passwordConfirm = passwordConfirm) }

    fun togglePasswordVisibility() =
        updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }

    fun toggleConfirmPasswordVisibility() =
        updateState { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }

    fun nextStep() {
        when (currentState.step) {
            SignupStep.AccountInfo -> viewModelScope.launch {
                val email = currentState.email
                updateState { it.copy(isLoading = true, errorMessage = null) }
                checkEmailAvailabilityUseCase(email = email)
                    .onSuccess { available ->
                        if (available) {
                            updateState { it.copy(step = SignupStep.PersonalInfo) }
                        } else {
                            updateState { it.copy(errorMessage = "이미 사용 중인 이메일이에요.") }
                        }
                    }
                    .onFailure {
                        updateState { it.copy(errorMessage = "이메일 확인에 실패했어요. 잠시 후 다시 시도해 주세요.") }
                    }
                updateState { it.copy(isLoading = false) }
            }
            SignupStep.PersonalInfo -> viewModelScope.launch {
                val current = currentState
                updateState { it.copy(isLoading = true, errorMessage = null) }
                val y = current.birthYear.toIntOrNull() ?: 0
                val m = current.birthMonth
                val d = current.birthDay.toIntOrNull() ?: 0
                val birthDate = "%04d-%02d-%02d".format(y, m, d)
                signupUseCase(
                    name = current.name,
                    email = current.email,
                    password = current.password,
                    passwordConfirm = current.passwordConfirm,
                    birthDate = birthDate,
                    gender = current.gender,
                    termsAgreed = current.termsAgreed,
                    privacyAgreed = current.privacyAgreed,
                    marketingAgreed = current.marketingAgreed,
                ).onSuccess {
                    updateState { it.copy(step = SignupStep.EmailVerify, code = "", remainingSeconds = 300) }
                    startTimer()
                }.onFailure { throwable ->
                    val message = when (throwable) {
                        is LbTripAuthException.InvalidInputValueException -> throwable.fields.firstNotNullOfOrNull { field ->
                            when (field) {
                                "email" -> "올바른 이메일 형식이 아니에요."
                                "password" -> "비밀번호는 영문·숫자 포함 8자 이상이어야 해요."
                                "passwordConfirm" -> "비밀번호가 일치하지 않아요."
                                "name" -> "이름을 확인해 주세요."
                                "birthDate" -> "생년월일을 확인해 주세요."
                                else -> null
                            }
                        } ?: "입력값을 다시 확인해 주세요."
                        is LbTripAuthException.PasswordConfirmMismatchException -> "비밀번호가 일치하지 않아요."
                        is LbTripAuthException.RequiredAgreementNotAcceptedException -> "필수 약관에 동의해 주세요."
                        is LbTripAuthException.DuplicateEmailException -> "이미 사용 중인 이메일이에요."
                        else -> "회원가입에 실패했어요. 잠시 후 다시 시도해 주세요."
                    }
                    updateState { it.copy(errorMessage = message) }
                }
                updateState { it.copy(isLoading = false) }
            }
            else -> Unit
        }
    }

    fun updateName(name: String) = updateState { it.copy(name = name) }

    fun updateBirthYear(year: String) = updateState { it.copy(birthYear = year) }

    fun updateBirthMonth(month: Int) = updateState { it.copy(birthMonth = month) }

    fun updateBirthDay(day: String) = updateState { it.copy(birthDay = day) }

    fun updateGender(gender: Gender) = updateState { it.copy(gender = gender) }

    fun toggleTos() = updateState { it.copy(termsAgreed = !it.termsAgreed) }

    fun togglePrivacy() = updateState { it.copy(privacyAgreed = !it.privacyAgreed) }

    fun toggleMarketing() = updateState { it.copy(marketingAgreed = !it.marketingAgreed) }

    fun toggleAllTerms() = updateState {
        val allOn = it.termsAgreed && it.privacyAgreed && it.marketingAgreed
        it.copy(termsAgreed = !allOn, privacyAgreed = !allOn, marketingAgreed = !allOn)
    }

    fun updateCode(code: String) = updateState { it.copy(code = code) }

    fun resendCode() {
        viewModelScope.launch {
            val email = currentState.email
            updateState { it.copy(isLoading = true, errorMessage = null) }
            resendEmailVerificationUseCase(email = email)
                .onSuccess {
                    updateState { it.copy(remainingSeconds = 300) }
                    startTimer()
                }
                .onFailure {
                    updateState { it.copy(errorMessage = "코드 재전송에 실패했어요.") }
                }
            updateState { it.copy(isLoading = false) }
        }
    }

    fun confirmCode() {
        viewModelScope.launch {
            val code = currentState.code
            updateState { it.copy(isLoading = true, errorMessage = null) }
            confirmEmailVerificationUseCase(code = code)
                .onSuccess {
                    timerJob?.cancel()
                    updateState { it.copy(step = SignupStep.Complete) }
                }
                .onFailure { throwable ->
                    val message = when (throwable) {
                        is LbTripAuthException.EmailVerificationCodeExpiredException -> "인증 코드가 만료됐어요."
                        is LbTripAuthException.EmailVerificationCodeUsedException -> "이미 사용된 인증 코드예요."
                        is LbTripAuthException.EmailVerificationCodeNotFoundException -> "인증 코드가 올바르지 않아요."
                        else -> "인증에 실패했어요. 잠시 후 다시 시도해 주세요."
                    }
                    updateState { it.copy(errorMessage = message) }
                }
            updateState { it.copy(isLoading = false) }
        }
    }

    fun navigateToSignin() = postSideEffect(SignupSideEffect.NavigateToSignin)

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
