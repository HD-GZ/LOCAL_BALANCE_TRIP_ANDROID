package live.lb_trip.feature.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState(eventSink = ::onEvent))
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    private val _effects = Channel<SignupEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var timerJob: Job? = null

    private fun onEvent(event: SignupEvent) {
        when (event) {
            SignupEvent.NavigateBack -> when (_uiState.value.step) {
                SignupStep.AccountInfo -> viewModelScope.launch { _effects.send(SignupEffect.NavigateBack) }
                SignupStep.PersonalInfo -> _uiState.update { it.copy(step = SignupStep.AccountInfo) }
                SignupStep.EmailVerify -> {
                    timerJob?.cancel()
                    _uiState.update { it.copy(step = SignupStep.PersonalInfo) }
                }
                SignupStep.Complete -> viewModelScope.launch { _effects.send(SignupEffect.NavigateBack) }
            }
            is SignupEvent.UpdateEmail -> _uiState.update { it.copy(email = event.email) }
            is SignupEvent.UpdatePassword -> _uiState.update { it.copy(password = event.password) }
            is SignupEvent.UpdatePasswordConfirm -> _uiState.update { it.copy(passwordConfirm = event.passwordConfirm) }
            SignupEvent.TogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            SignupEvent.ToggleConfirmPasswordVisibility -> _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            SignupEvent.NextStep -> when (_uiState.value.step) {
                SignupStep.AccountInfo -> viewModelScope.launch {
                    val email = _uiState.value.email
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    checkEmailAvailabilityUseCase(email = email)
                        .onSuccess { available ->
                            if (available) {
                                _uiState.update { it.copy(step = SignupStep.PersonalInfo) }
                            } else {
                                _uiState.update { it.copy(errorMessage = "이미 사용 중인 이메일이에요.") }
                            }
                        }
                        .onFailure {
                            _uiState.update { it.copy(errorMessage = "이메일 확인에 실패했어요. 잠시 후 다시 시도해 주세요.") }
                        }
                    _uiState.update { it.copy(isLoading = false) }
                }
                SignupStep.PersonalInfo -> viewModelScope.launch {
                    val current = _uiState.value
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
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
                        _uiState.update { it.copy(step = SignupStep.EmailVerify, code = "", remainingSeconds = 300) }
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
                        _uiState.update { it.copy(errorMessage = message) }
                    }
                    _uiState.update { it.copy(isLoading = false) }
                }
                else -> Unit
            }
            is SignupEvent.UpdateName -> _uiState.update { it.copy(name = event.name) }
            is SignupEvent.UpdateBirthYear -> _uiState.update { it.copy(birthYear = event.year) }
            is SignupEvent.UpdateBirthMonth -> _uiState.update { it.copy(birthMonth = event.month) }
            is SignupEvent.UpdateBirthDay -> _uiState.update { it.copy(birthDay = event.day) }
            is SignupEvent.UpdateGender -> _uiState.update { it.copy(gender = event.gender) }
            SignupEvent.ToggleTos -> _uiState.update { it.copy(termsAgreed = !it.termsAgreed) }
            SignupEvent.TogglePrivacy -> _uiState.update { it.copy(privacyAgreed = !it.privacyAgreed) }
            SignupEvent.ToggleMarketing -> _uiState.update { it.copy(marketingAgreed = !it.marketingAgreed) }
            SignupEvent.ToggleAllTerms -> _uiState.update {
                val allOn = it.termsAgreed && it.privacyAgreed && it.marketingAgreed
                it.copy(termsAgreed = !allOn, privacyAgreed = !allOn, marketingAgreed = !allOn)
            }
            is SignupEvent.UpdateCode -> _uiState.update { it.copy(code = event.code) }
            SignupEvent.ResendCode -> viewModelScope.launch {
                val email = _uiState.value.email
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                resendEmailVerificationUseCase(email = email)
                    .onSuccess {
                        _uiState.update { it.copy(remainingSeconds = 300) }
                        startTimer()
                    }
                    .onFailure {
                        _uiState.update { it.copy(errorMessage = "코드 재전송에 실패했어요.") }
                    }
                _uiState.update { it.copy(isLoading = false) }
            }
            SignupEvent.ConfirmCode -> viewModelScope.launch {
                val code = _uiState.value.code
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                confirmEmailVerificationUseCase(code = code)
                    .onSuccess {
                        timerJob?.cancel()
                        _uiState.update { it.copy(step = SignupStep.Complete) }
                    }
                    .onFailure { throwable ->
                        val message = when (throwable) {
                            is LbTripAuthException.EmailVerificationCodeExpiredException -> "인증 코드가 만료됐어요."
                            is LbTripAuthException.EmailVerificationCodeUsedException -> "이미 사용된 인증 코드예요."
                            is LbTripAuthException.EmailVerificationCodeNotFoundException -> "인증 코드가 올바르지 않아요."
                            else -> "인증에 실패했어요. 잠시 후 다시 시도해 주세요."
                        }
                        _uiState.update { it.copy(errorMessage = message) }
                    }
                _uiState.update { it.copy(isLoading = false) }
            }
            SignupEvent.NavigateToSignin -> viewModelScope.launch { _effects.send(SignupEffect.NavigateToSignin) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(1_000)
                _uiState.update { it.copy(remainingSeconds = (it.remainingSeconds - 1).coerceAtLeast(0)) }
            }
        }
    }
}
