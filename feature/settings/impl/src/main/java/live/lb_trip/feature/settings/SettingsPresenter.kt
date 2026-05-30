package live.lb_trip.feature.settings

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SettingsPresenter @AssistedInject constructor(
    @Assisted private val screen: SettingsScreen
) : Presenter<SettingsState> {

    @AssistedFactory
    interface Factory {
        fun create(screen: SettingsScreen): SettingsPresenter
    }

    @Composable
    override fun present(): SettingsState {
        return SettingsState(
            title = "Settings Screen Skeleton"
        )
    }
}
