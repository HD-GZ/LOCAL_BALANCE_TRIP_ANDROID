package live.lb_trip.feature.signin

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

class SigninPresenterFactory @Inject constructor(
    private val presenterFactory: SigninPresenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? =
        when (screen) {
            is SigninScreen -> presenterFactory.create(screen, navigator)
            else -> null
        }
}

class SigninUiFactory @Inject constructor() : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext,
    ): Ui<*>? =
        when (screen) {
            is SigninScreen -> SigninUi()
            else -> null
        }
}

@Module
@InstallIn(SingletonComponent::class)
interface SigninModule {
    @Binds
    @IntoSet
    fun bindSigninPresenterFactory(factory: SigninPresenterFactory): Presenter.Factory

    @Binds
    @IntoSet
    fun bindSigninUiFactory(factory: SigninUiFactory): Ui.Factory
}
