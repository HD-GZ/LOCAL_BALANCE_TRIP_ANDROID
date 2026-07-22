package live.lb_trip.feature.tour

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.tourScreen(onBack: () -> Unit) {
    composable<TourRoute> {
        TourScreen(onBack = onBack)
    }
}
