package live.lb_trip.localbalancetrip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import live.lb_trip.core.designsystem.LocalBalanceTripTheme
import live.lb_trip.feature.home.HomeScreen
import live.lb_trip.feature.onboarding.OnboardingScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var presenterFactories: Set<@JvmSuppressWildcards Presenter.Factory>

    @Inject
    lateinit var uiFactories: Set<@JvmSuppressWildcards Ui.Factory>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val circuit = Circuit.Builder()
            .addPresenterFactories(presenterFactories)
            .addUiFactories(uiFactories)
            .build()

        setContent {
            CircuitCompositionLocals(circuit) {
                LocalBalanceTripTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navStack = rememberSaveableBackStack(OnboardingScreen)
                        val navigator = rememberCircuitNavigator(navStack)
                        NavigableCircuitContent(
                            navigator = navigator,
                            navStack = navStack
                        )
                    }
                }
            }
        }
    }
}
