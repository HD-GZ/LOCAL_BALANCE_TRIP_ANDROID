package live.lb_trip.feature.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SigninPresenter @AssistedInject constructor(
    @Assisted private val screen: SigninScreen,
    @Assisted private val navigator: Navigator,
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

        return SigninState(
            email = email,
            password = password,
            isPasswordVisible = isPasswordVisible,
        ) { event ->
            when (event) {
                SigninEvent.NavigateBack -> navigator.pop()
                is SigninEvent.UpdateEmail -> email = event.email
                is SigninEvent.UpdatePassword -> password = event.password
                SigninEvent.TogglePasswordVisibility -> isPasswordVisible = !isPasswordVisible
                SigninEvent.Login -> Unit // TODO
                SigninEvent.NavigateToForgotPassword -> Unit // TODO
                SigninEvent.NavigateToSignup -> Unit // TODO
            }
        }
    }
}
