package live.lb_trip.feature.signin

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.auth.LbTripAuthException
import live.lb_trip.domain.usecase.LoginUseCase

@HiltViewModel
class SigninViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : BaseViewModel<SigninUiState, Nothing>(SigninUiState()) {

    fun updateEmail(email: String) {
        updateState { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        updateState { it.copy(password = password) }
    }

    fun togglePasswordVisibility() {
        updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            loginUseCase(email = currentState.email, password = currentState.password)
                .onFailure { throwable ->
                    val message = when (throwable) {
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
                    updateState { it.copy(errorMessage = message) }
                }
            updateState { it.copy(isLoading = false) }
        }
    }
}
