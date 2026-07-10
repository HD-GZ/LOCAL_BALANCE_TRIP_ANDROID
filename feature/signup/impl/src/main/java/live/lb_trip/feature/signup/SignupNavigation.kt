package live.lb_trip.feature.signup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.signupScreen(
    onBack: () -> Unit,
    onNavigateToSignin: () -> Unit,
) {
    composable<SignupRoute> {
        SignupScreen(
            onBack = onBack,
            onNavigateToSignin = onNavigateToSignin,
        )
    }
}
