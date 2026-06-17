package live.lb_trip.feature.onboarding

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import live.lb_trip.feature.signin.SigninScreen
import live.lb_trip.feature.signup.SignupScreen

class OnboardingPresenter @AssistedInject constructor(
    @Assisted private val screen: OnboardingScreen,
    @Assisted private val navigator: Navigator,
) : Presenter<OnboardingState> {

    @AssistedFactory
    interface Factory {
        fun create(screen: OnboardingScreen, navigator: Navigator): OnboardingPresenter
    }

    @Composable
    override fun present(): OnboardingState {
        return OnboardingState { event ->
            when (event) {
                OnboardingEvent.NavigateToSignup -> navigator.goTo(SignupScreen)
                OnboardingEvent.NavigateToSignin -> navigator.goTo(SigninScreen)
            }
        }
    }
}
