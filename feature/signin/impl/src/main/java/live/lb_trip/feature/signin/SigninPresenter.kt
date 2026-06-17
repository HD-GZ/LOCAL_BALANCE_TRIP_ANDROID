package live.lb_trip.feature.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import live.lb_trip.domain.exception.auth.LbTripAuthException
import live.lb_trip.domain.usecase.LoginUseCase
import live.lb_trip.feature.home.HomeScreen
import live.lb_trip.feature.signup.SignupScreen

class SigninPresenter @AssistedInject constructor(
    @Assisted private val screen: SigninScreen,
    @Assisted private val navigator: Navigator,
    private val loginUseCase: LoginUseCase,
) : Presenter<SigninState> {

    @AssistedFactory
    interface Factory {
        fun create(screen: SigninScreen, navigator: Navigator): SigninPresenter
    }

    @Composable
    override fun present(): SigninState {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()

        return SigninState(
            email = email,
            password = password,
            isPasswordVisible = isPasswordVisible,
            isLoading = isLoading,
            errorMessage = errorMessage,
        ) { event ->
            when (event) {
                SigninEvent.NavigateBack -> navigator.pop()
                is SigninEvent.UpdateEmail -> email = event.email
                is SigninEvent.UpdatePassword -> password = event.password

                SigninEvent.TogglePasswordVisibility -> isPasswordVisible = !isPasswordVisible
                SigninEvent.Login -> scope.launch {
                    isLoading = true
                    errorMessage = null
                    loginUseCase(email = email, password = password)
                        .onSuccess {
                            navigator.resetRoot(HomeScreen)
                        }
                        .onFailure { throwable ->
                            errorMessage = when (throwable) {
                                is LbTripAuthException.InvalidCredentialsException ->
                                    "이메일 또는 비밀번호가 올바르지 않아요."

                                is LbTripAuthException.EmailNotVerifiedException ->
                                    "이메일 인증이 필요해요. 가입 시 받은 인증 메일을 확인해 주세요."

                                is LbTripAuthException.UserNotFoundException ->
                                    "가입되지 않은 이메일이에요."

                                is LbTripAuthException.UnauthorizedException ->
                                    "인증이 만료됐어요. 다시 로그인해 주세요."

                                else -> "로그인에 실패했어요. 잠시 후 다시 시도해 주세요."
                            }
                        }
                    isLoading = false
                }

                SigninEvent.NavigateToForgotPassword -> Unit // TODO
                SigninEvent.NavigateToSignup -> navigator.goTo(SignupScreen)
            }
        }
    }
}
