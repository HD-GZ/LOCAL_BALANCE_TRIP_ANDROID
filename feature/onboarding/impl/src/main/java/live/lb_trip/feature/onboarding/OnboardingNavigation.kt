package live.lb_trip.feature.onboarding

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

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
