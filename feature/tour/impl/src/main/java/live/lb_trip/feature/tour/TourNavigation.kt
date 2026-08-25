package live.lb_trip.feature.tour

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.tourScreen(onBack: () -> Unit, onTourFinished: (showReport: Boolean) -> Unit) {
    composable<TourRoute> {
        TourScreen(onBack = onBack, onTourFinished = onTourFinished)
    }
}
