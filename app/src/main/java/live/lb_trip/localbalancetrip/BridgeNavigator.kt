package live.lb_trip.localbalancetrip

import androidx.navigation.NavController
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.navigation.NavStackList
import com.slack.circuit.runtime.screen.PopResult
import com.slack.circuit.runtime.screen.Screen
import live.lb_trip.feature.home.HomeRoute
import live.lb_trip.feature.home.HomeScreen
import live.lb_trip.feature.onboarding.OnboardingScreen
import live.lb_trip.feature.propensity.PropensityScreen
import live.lb_trip.feature.settings.SettingsScreen
import live.lb_trip.feature.signin.SigninRoute
import live.lb_trip.feature.signin.SigninScreen
import live.lb_trip.feature.signup.SignupScreen

/**
 * Adapts Circuit's [Navigator] interface to an androidx Navigation-Compose [NavController].
 * Temporary bridge used while features are migrated off Circuit one at a time; every existing
 * Circuit `Screen` object maps to one `@Serializable` route object in Routes.kt.
 */
class BridgeNavigator(private val navController: NavController) : Navigator {

    override fun goTo(screen: Screen): Boolean {
        navController.navigate(routeFor(screen))
        return true
    }

    override fun forward(): Boolean = false

    override fun backward(): Boolean = false

    override fun pop(result: PopResult?): Screen? {
        navController.popBackStack()
        return null
    }

    override fun peek(): Screen? = null

    override fun peekBackStack(): List<Screen> = emptyList()

    override fun peekNavStack(): NavStackList<Screen>? = null

    override fun resetRoot(newRoot: Screen, options: Navigator.StateOptions): List<Screen> {
        navController.navigate(routeFor(newRoot)) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
        return emptyList()
    }

    private fun routeFor(screen: Screen): Any = when (screen) {
        is HomeScreen -> HomeRoute
        is SettingsScreen -> SettingsRoute
        is SigninScreen -> SigninRoute
        is SignupScreen -> SignupRoute
        is OnboardingScreen -> OnboardingRoute
        is PropensityScreen -> PropensityRoute
        else -> error("BridgeNavigator: unmapped screen $screen")
    }
}
