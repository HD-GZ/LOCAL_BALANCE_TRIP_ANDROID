package live.lb_trip.feature.signup

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.auth.LbTripAuthException
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
) : BaseViewModel<SignupUiState, SignupIntent, SignupSideEffect>(SignupUiState()) {

    private var timerJob: Job? = null

    override fun onIntent(intent: SignupIntent) {
        when (intent) {
            is SignupIntent.AccountInfo -> handleAccountInfoIntent(intent)
            is SignupIntent.PersonalInfo -> handlePersonalInfoIntent(intent)
            is SignupIntent.EmailVerify -> handleEmailVerifyIntent(intent)
            SignupIntent.NavigateToSigninClicked -> postSideEffect(SignupSideEffect.NavigateToSignin)
        }
    }

    private fun handleAccountInfoIntent(intent: SignupIntent.AccountInfo) {
        when (intent) {
            is SignupIntent.EmailChanged -> updateState { it.copy(email = intent.value) }
            is SignupIntent.PasswordChanged -> updateState { it.copy(password = intent.value) }
            SignupIntent.TogglePasswordVisibility ->
                updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            is SignupIntent.PasswordConfirmChanged -> updateState { it.copy(passwordConfirm = intent.value) }
            SignupIntent.ToggleConfirmPasswordVisibility ->
                updateState { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            SignupIntent.AccountInfoNextStepClicked -> checkEmailAndProceed()
        }
    }

    private fun handlePersonalInfoIntent(intent: SignupIntent.PersonalInfo) {
        when (intent) {
            is SignupIntent.NameChanged -> updateState { it.copy(name = intent.value) }
            is SignupIntent.BirthYearChanged -> updateState { it.copy(birthYear = intent.value) }
            is SignupIntent.BirthMonthChanged -> updateState { it.copy(birthMonth = intent.value) }
            is SignupIntent.BirthDayChanged -> updateState { it.copy(birthDay = intent.value) }
            is SignupIntent.GenderChanged -> updateState { it.copy(gender = intent.value) }
            SignupIntent.ToggleTos -> updateState { it.copy(termsAgreed = !it.termsAgreed) }
            SignupIntent.TogglePrivacy -> updateState { it.copy(privacyAgreed = !it.privacyAgreed) }
            SignupIntent.ToggleMarketing -> updateState { it.copy(marketingAgreed = !it.marketingAgreed) }
            SignupIntent.ToggleAllTerms -> updateState {
                val allOn = it.termsAgreed && it.privacyAgreed && it.marketingAgreed
                it.copy(termsAgreed = !allOn, privacyAgreed = !allOn, marketingAgreed = !allOn)
            }
            SignupIntent.PersonalInfoNextStepClicked -> submitPersonalInfo()
        }
    }

    private fun handleEmailVerifyIntent(intent: SignupIntent.EmailVerify) {
        when (intent) {
            is SignupIntent.CodeChanged -> updateState { it.copy(code = intent.value) }
            SignupIntent.ResendCodeClicked -> resendCode()
            SignupIntent.ConfirmCodeClicked -> confirmCode()
            SignupIntent.EmailVerifyStepLeft -> timerJob?.cancel()
        }
    }

    private fun checkEmailAndProceed() {
        viewModelScope.launch {
            val email = currentState.email
            updateState { it.copy(isLoading = true, errorMessage = null) }
            checkEmailAvailabilityUseCase(email = email)
                .onSuccess { available ->
                    if (available) {
                        postSideEffect(SignupSideEffect.NavigateToPersonalInfo)
                    } else {
                        updateState { it.copy(errorMessage = "이미 사용 중인 이메일이에요.") }
                    }
                }
                .onFailure {
                    updateState { it.copy(errorMessage = "이메일 확인에 실패했어요. 잠시 후 다시 시도해 주세요.") }
                }
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun submitPersonalInfo() {
        viewModelScope.launch {
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
                updateState { it.copy(code = "", remainingSeconds = 300) }
                startTimer()
                postSideEffect(SignupSideEffect.NavigateToEmailVerify)
            }.onFailure { throwable ->
                updateState { it.copy(errorMessage = signupFailureMessage(throwable)) }
            }
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun signupFailureMessage(throwable: Throwable): String = when (throwable) {
        is LbTripAuthException.InvalidInputValueException -> throwable.fields.firstNotNullOfOrNull { field ->
            invalidFieldMessage(field)
        } ?: "입력값을 다시 확인해 주세요."
        is LbTripAuthException.PasswordConfirmMismatchException -> "비밀번호가 일치하지 않아요."
        is LbTripAuthException.RequiredAgreementNotAcceptedException -> "필수 약관에 동의해 주세요."
        is LbTripAuthException.DuplicateEmailException -> "이미 사용 중인 이메일이에요."
        else -> "회원가입에 실패했어요. 잠시 후 다시 시도해 주세요."
    }

    private fun invalidFieldMessage(field: String): String? = when (field) {
        "email" -> "올바른 이메일 형식이 아니에요."
        "password" -> "비밀번호는 영문·숫자 포함 8자 이상이어야 해요."
        "passwordConfirm" -> "비밀번호가 일치하지 않아요."
        "name" -> "이름을 확인해 주세요."
        "birthDate" -> "생년월일을 확인해 주세요."
        else -> null
    }

    private fun resendCode() {
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

    private fun confirmCode() {
        viewModelScope.launch {
            val code = currentState.code
            updateState { it.copy(isLoading = true, errorMessage = null) }
            confirmEmailVerificationUseCase(code = code)
                .onSuccess {
                    timerJob?.cancel()
                    postSideEffect(SignupSideEffect.NavigateToComplete)
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
