package live.lb_trip.feature.signup

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

class SignupPresenterFactory @Inject constructor(
    private val presenterFactory: SignupPresenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? =
        when (screen) {
            is SignupScreen -> presenterFactory.create(screen, navigator)
            else -> null
        }
}

class SignupUiFactory @Inject constructor() : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext,
    ): Ui<*>? =
        when (screen) {
            is SignupScreen -> SignupUi()
            else -> null
        }
}

@Module
@InstallIn(SingletonComponent::class)
interface SignupModule {
    @Binds
    @IntoSet
    fun bindSignupPresenterFactory(factory: SignupPresenterFactory): Presenter.Factory

    @Binds
    @IntoSet
    fun bindSignupUiFactory(factory: SignupUiFactory): Ui.Factory
}
