package live.lb_trip.feature.signin

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.signinScreen(
    onBack: () -> Unit,
    onNavigateToSignup: () -> Unit,
) {
    composable<SigninRoute> {
        SigninScreen(
            onBack = onBack,
            onNavigateToSignup = onNavigateToSignup,
        )
    }
}
