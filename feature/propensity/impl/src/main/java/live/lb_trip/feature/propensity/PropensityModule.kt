package live.lb_trip.feature.propensity

import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

class PropensityPresenterFactory @Inject constructor(
    private val presenterFactory: PropensityPresenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? =
        when (screen) {
            is PropensityScreen -> presenterFactory.create(screen, navigator)
            else -> null
        }
}

class PropensityUiFactory @Inject constructor() : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext,
    ): Ui<*>? =
        when (screen) {
            is PropensityScreen -> PropensityUi()
            else -> null
        }
}

@Module
@InstallIn(SingletonComponent::class)
interface PropensityModule {
    @Binds
    @IntoSet
    fun bindPropensityPresenterFactory(factory: PropensityPresenterFactory): Presenter.Factory

    @Binds
    @IntoSet
    fun bindPropensityUiFactory(factory: PropensityUiFactory): Ui.Factory
}
