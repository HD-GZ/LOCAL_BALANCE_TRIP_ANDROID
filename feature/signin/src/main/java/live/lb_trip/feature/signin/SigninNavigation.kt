package live.lb_trip.feature.signin

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object SigninRoute

fun NavGraphBuilder.signinScreen(
    onBack: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit,
) {
    composable<SigninRoute> {
        SigninScreen(
            onBack = onBack,
            onNavigateToSignup = onNavigateToSignup,
            onLoginSuccess = onLoginSuccess,
        )
    }
}
