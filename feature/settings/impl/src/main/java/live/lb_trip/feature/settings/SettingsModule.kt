package live.lb_trip.feature.settings

import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.screen.Screen
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

class SettingsPresenterFactory @Inject constructor(
    private val presenterFactory: SettingsPresenter.Factory
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext
    ): Presenter<*>? {
        return when (screen) {
            is SettingsScreen -> presenterFactory.create(screen)
            else -> null
        }
    }
}

class SettingsUiFactory @Inject constructor() : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext
    ): Ui<*>? {
        return when (screen) {
            is SettingsScreen -> SettingsUi()
            else -> null
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface SettingsModule {
    @Binds
    @IntoSet
    fun bindSettingsPresenterFactory(factory: SettingsPresenterFactory): Presenter.Factory

    @Binds
    @IntoSet
    fun bindSettingsUiFactory(factory: SettingsUiFactory): Ui.Factory
}
