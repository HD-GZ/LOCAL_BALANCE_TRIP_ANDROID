package live.lb_trip.feature.signin

import androidx.compose.runtime.Composable
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
        return SigninState()
    }
}
