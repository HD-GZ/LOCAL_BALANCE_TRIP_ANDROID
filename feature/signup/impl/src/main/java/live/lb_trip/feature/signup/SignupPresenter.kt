package live.lb_trip.feature.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import live.lb_trip.domain.exception.auth.LbTripAuthException
import live.lb_trip.domain.model.Gender
import live.lb_trip.domain.usecase.ConfirmEmailVerificationUseCase
import live.lb_trip.domain.usecase.ResendEmailVerificationUseCase
import live.lb_trip.domain.usecase.SignupUseCase
import live.lb_trip.feature.signin.SigninScreen

class SignupPresenter @AssistedInject constructor(
    @Assisted private val screen: SignupScreen,
    @Assisted private val navigator: Navigator,
    private val signupUseCase: SignupUseCase,
    private val confirmEmailVerificationUseCase: ConfirmEmailVerificationUseCase,
    private val resendEmailVerificationUseCase: ResendEmailVerificationUseCase,
) : Presenter<SignupState> {

    @AssistedFactory
    interface Factory {
        fun create(screen: SignupScreen, navigator: Navigator): SignupPresenter
    }

    @Composable
    override fun present(): SignupState {
        var step by remember { mutableStateOf(SignupStep.AccountInfo) }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordConfirm by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }
        var isConfirmPasswordVisible by remember { mutableStateOf(false) }
        var name by remember { mutableStateOf("") }
        var birthYear by remember { mutableStateOf("") }
        var birthMonth by remember { mutableIntStateOf(0) }
        var birthDay by remember { mutableStateOf("") }
        var gender by remember { mutableStateOf(Gender.NOT_SPECIFIED) }
        var termsAgreed by remember { mutableStateOf(false) }
        var privacyAgreed by remember { mutableStateOf(false) }
        var marketingAgreed by remember { mutableStateOf(false) }
        var code by remember { mutableStateOf("") }
        var remainingSeconds by remember { mutableIntStateOf(300) }
        var timerKey by remember { mutableIntStateOf(0) }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(step, timerKey) {
            if (step == SignupStep.EmailVerify) {
                while (remainingSeconds > 0) {
                    delay(1_000)
                    remainingSeconds = (remainingSeconds - 1).coerceAtLeast(0)
                }
            }
        }

        return SignupState(
            step = step,
            email = email,
            password = password,
            passwordConfirm = passwordConfirm,
            isPasswordVisible = isPasswordVisible,
            isConfirmPasswordVisible = isConfirmPasswordVisible,
            name = name,
            birthYear = birthYear,
            birthMonth = birthMonth,
            birthDay = birthDay,
            gender = gender,
            termsAgreed = termsAgreed,
            privacyAgreed = privacyAgreed,
            marketingAgreed = marketingAgreed,
            code = code,
            remainingSeconds = remainingSeconds,
            isLoading = isLoading,
            errorMessage = errorMessage,
        ) { event ->
            when (event) {
                SignupEvent.NavigateBack -> when (step) {
                    SignupStep.AccountInfo -> navigator.pop()
                    SignupStep.PersonalInfo -> step = SignupStep.AccountInfo
                    SignupStep.EmailVerify -> step = SignupStep.PersonalInfo
                    SignupStep.Complete -> navigator.pop()
                }
                is SignupEvent.UpdateEmail -> email = event.email
                is SignupEvent.UpdatePassword -> password = event.password
                is SignupEvent.UpdatePasswordConfirm -> passwordConfirm = event.passwordConfirm
                SignupEvent.TogglePasswordVisibility -> isPasswordVisible = !isPasswordVisible
                SignupEvent.ToggleConfirmPasswordVisibility -> isConfirmPasswordVisible = !isConfirmPasswordVisible
                SignupEvent.NextStep -> when (step) {
                    SignupStep.AccountInfo -> step = SignupStep.PersonalInfo
                    SignupStep.PersonalInfo -> scope.launch {
                        isLoading = true
                        errorMessage = null
                        val y = birthYear.toIntOrNull() ?: 0
                        val m = birthMonth
                        val d = birthDay.toIntOrNull() ?: 0
                        val birthDate = "%04d-%02d-%02d".format(y, m, d)
                        signupUseCase(
                            name = name,
                            email = email,
                            password = password,
                            passwordConfirm = passwordConfirm,
                            birthDate = birthDate,
                            gender = gender,
                            termsAgreed = termsAgreed,
                            privacyAgreed = privacyAgreed,
                            marketingAgreed = marketingAgreed,
                        ).onSuccess {
                            step = SignupStep.EmailVerify
                            code = ""
                            remainingSeconds = 300
                            timerKey++
                        }.onFailure { throwable ->
                            errorMessage = when (throwable) {
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
                        }
                        isLoading = false
                    }
                    else -> {}
                }
                is SignupEvent.UpdateName -> name = event.name
                is SignupEvent.UpdateBirthYear -> birthYear = event.year
                is SignupEvent.UpdateBirthMonth -> birthMonth = event.month
                is SignupEvent.UpdateBirthDay -> birthDay = event.day
                is SignupEvent.UpdateGender -> gender = event.gender
                SignupEvent.ToggleTos -> termsAgreed = !termsAgreed
                SignupEvent.TogglePrivacy -> privacyAgreed = !privacyAgreed
                SignupEvent.ToggleMarketing -> marketingAgreed = !marketingAgreed
                SignupEvent.ToggleAllTerms -> {
                    val allOn = termsAgreed && privacyAgreed && marketingAgreed
                    termsAgreed = !allOn
                    privacyAgreed = !allOn
                    marketingAgreed = !allOn
                }
                is SignupEvent.UpdateCode -> code = event.code
                SignupEvent.ResendCode -> scope.launch {
                    isLoading = true
                    errorMessage = null
                    resendEmailVerificationUseCase(email = email)
                        .onSuccess {
                            remainingSeconds = 300
                            timerKey++
                        }
                        .onFailure {
                            errorMessage = "코드 재전송에 실패했어요."
                        }
                    isLoading = false
                }
                SignupEvent.ConfirmCode -> scope.launch {
                    isLoading = true
                    errorMessage = null
                    confirmEmailVerificationUseCase(code = code)
                        .onSuccess {
                            step = SignupStep.Complete
                        }
                        .onFailure { throwable ->
                            errorMessage = when (throwable) {
                                is LbTripAuthException.EmailVerificationCodeExpiredException -> "인증 코드가 만료됐어요."
                                is LbTripAuthException.EmailVerificationCodeUsedException -> "이미 사용된 인증 코드예요."
                                is LbTripAuthException.EmailVerificationCodeNotFoundException -> "인증 코드가 올바르지 않아요."
                                else -> "인증에 실패했어요. 잠시 후 다시 시도해 주세요."
                            }
                        }
                    isLoading = false
                }
                SignupEvent.NavigateToSignin -> navigator.goTo(SigninScreen)
            }
        }
    }
}
