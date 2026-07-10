package live.lb_trip.feature.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import live.lb_trip.domain.exception.auth.LbTripAuthException
import live.lb_trip.domain.usecase.LoginUseCase

@HiltViewModel
class SigninViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SigninUiState())
    val uiState: StateFlow<SigninUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<SigninSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    private fun postSideEffect(sideEffect: SigninSideEffect) {
        viewModelScope.launch { _sideEffect.send(sideEffect) }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            loginUseCase(email = _uiState.value.email, password = _uiState.value.password)
                .onSuccess {
                    postSideEffect(SigninSideEffect.LoginSucceeded)
                }
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
                    _uiState.update { it.copy(errorMessage = message) }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
