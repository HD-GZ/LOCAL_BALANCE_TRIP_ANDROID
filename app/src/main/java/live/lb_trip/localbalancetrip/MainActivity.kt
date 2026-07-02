package live.lb_trip.localbalancetrip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.isLoggedIn.value == null }

        val circuit = Circuit.Builder()
            .addPresenterFactories(presenterFactories)
            .addUiFactories(uiFactories)
            .build()

        setContent {
            val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

            if (isLoggedIn == null) return@setContent

            CircuitCompositionLocals(circuit) {
                LocalBalanceTripTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val initialScreen = if (isLoggedIn == true) HomeScreen else OnboardingScreen
                        val navStack = rememberSaveableBackStack(initialScreen)
                        val navigator = rememberCircuitNavigator(navStack)
                        NavigableCircuitContent(
                            navigator = navigator,
                            navStack = navStack,
                        )
                    }
                }
            }
        }
    }
}
