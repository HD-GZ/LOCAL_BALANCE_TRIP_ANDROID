package live.lb_trip.feature.signin

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.auth.LbTripAuthException
import live.lb_trip.domain.usecase.LoginUseCase

@HiltViewModel
class SigninViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val loginUseCase: LoginUseCase,
) : BaseViewModel<SigninUiState, SigninIntent, SigninSideEffect>(SigninUiState()) {

    override fun onIntent(intent: SigninIntent) {
        when (intent) {
            is SigninIntent.EmailChanged -> updateState { it.copy(email = intent.value) }
            is SigninIntent.PasswordChanged -> updateState { it.copy(password = intent.value) }
            SigninIntent.TogglePasswordVisibility ->
                updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            SigninIntent.LoginClicked -> login()
        }
    }

    private fun login() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            loginUseCase(email = currentState.email, password = currentState.password)
                .onSuccess { postSideEffect(SigninSideEffect.LoginSucceeded) }
                .onFailure { throwable ->
                    val message = when (throwable) {
                        is LbTripAuthException.InvalidCredentialsException ->
                            context.getString(R.string.signin_error_invalid_credentials)
                        is LbTripAuthException.EmailNotVerifiedException ->
                            context.getString(R.string.signin_error_email_not_verified)
                        is LbTripAuthException.UserNotFoundException ->
                            context.getString(R.string.signin_error_user_not_found)
                        is LbTripAuthException.UnauthorizedException ->
                            context.getString(R.string.signin_error_unauthorized)
                        else -> context.getString(R.string.signin_error_login_failed)
                    }
                    updateState { it.copy(errorMessage = message) }
                }
            updateState { it.copy(isLoading = false) }
        }
    }
}
