package live.lb_trip.feature.onboarding

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

class OnboardingPresenterFactory @Inject constructor(
    private val presenterFactory: OnboardingPresenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? =
        when (screen) {
            is OnboardingScreen -> presenterFactory.create(screen, navigator)
            else -> null
        }
}

class OnboardingUiFactory @Inject constructor() : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext,
    ): Ui<*>? =
        when (screen) {
            is OnboardingScreen -> OnboardingUi()
            else -> null
        }
}

@Module
@InstallIn(SingletonComponent::class)
interface OnboardingModule {
    @Binds
    @IntoSet
    fun bindOnboardingPresenterFactory(factory: OnboardingPresenterFactory): Presenter.Factory

    @Binds
    @IntoSet
    fun bindOnboardingUiFactory(factory: OnboardingUiFactory): Ui.Factory
}
