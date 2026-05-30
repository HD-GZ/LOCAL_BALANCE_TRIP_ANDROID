package live.lb_trip.feature.home

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

class HomePresenterFactory @Inject constructor(
    private val presenterFactory: HomePresenter.Factory
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext
    ): Presenter<*>? {
        return when (screen) {
            is HomeScreen -> presenterFactory.create(screen, navigator)
            else -> null
        }
    }
}

class HomeUiFactory @Inject constructor() : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext
    ): Ui<*>? {
        return when (screen) {
            is HomeScreen -> HomeUi()
            else -> null
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface HomeModule {
    @Binds
    @IntoSet
    fun bindHomePresenterFactory(factory: HomePresenterFactory): Presenter.Factory

    @Binds
    @IntoSet
    fun bindHomeUiFactory(factory: HomeUiFactory): Ui.Factory
}
