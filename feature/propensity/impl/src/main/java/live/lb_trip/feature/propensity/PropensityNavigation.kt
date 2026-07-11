package live.lb_trip.feature.propensity

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.propensityScreen(
    onBack: () -> Unit,
    onNavigateToRecommendation: () -> Unit,
) {
    composable<PropensityRoute> {
        PropensityScreen(onBack = onBack, onNavigateToRecommendation = onNavigateToRecommendation)
    }
}
