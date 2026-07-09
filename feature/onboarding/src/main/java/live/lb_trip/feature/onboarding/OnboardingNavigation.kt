package live.lb_trip.feature.onboarding

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object OnboardingRoute

fun NavGraphBuilder.onboardingScreen(
    onNavigateToSignup: () -> Unit,
    onNavigateToSignin: () -> Unit,
) {
    composable<OnboardingRoute> {
        OnboardingScreen(
            onSignupClick = onNavigateToSignup,
            onSigninClick = onNavigateToSignin,
        )
    }
}
