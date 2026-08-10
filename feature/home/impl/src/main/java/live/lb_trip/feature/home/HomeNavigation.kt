package live.lb_trip.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.policyListScreen(onBack: () -> Unit) {
    composable<PolicyListRoute> {
        PolicyListScreen(onBack = onBack)
    }
}
