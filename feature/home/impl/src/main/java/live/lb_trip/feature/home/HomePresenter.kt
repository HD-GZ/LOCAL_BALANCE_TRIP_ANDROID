package live.lb_trip.feature.home

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import live.lb_trip.feature.propensity.PropensityScreen
import live.lb_trip.feature.settings.SettingsScreen

class HomePresenter @AssistedInject constructor(
    @Assisted private val screen: HomeScreen,
    @Assisted private val navigator: Navigator,
) : Presenter<HomeState> {

    @AssistedFactory
    interface Factory {
        fun create(screen: HomeScreen, navigator: Navigator): HomePresenter
    }

    @Composable
    override fun present(): HomeState {
        return HomeState { event ->
            when (event) {
                HomeEvent.StartPropensityDiagnosis -> navigator.goTo(PropensityScreen)
                HomeEvent.NavigateToSettings -> navigator.goTo(SettingsScreen)
            }
        }
    }
}
