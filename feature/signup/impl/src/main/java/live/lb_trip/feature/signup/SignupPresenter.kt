package live.lb_trip.feature.signup

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SignupPresenter @AssistedInject constructor(
    @Assisted private val screen: SignupScreen,
    @Assisted private val navigator: Navigator,
) : Presenter<SignupState> {

    @AssistedFactory
    interface Factory {
        fun create(screen: SignupScreen, navigator: Navigator): SignupPresenter
    }

    @Composable
    override fun present(): SignupState {
        return SignupState()
    }
}
