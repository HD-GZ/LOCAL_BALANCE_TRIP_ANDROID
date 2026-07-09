package live.lb_trip.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

fun NavGraphBuilder.homeScreen(
    onStartDiagnosis: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onStartDiagnosisClick = onStartDiagnosis,
            onMyInfoClick = onNavigateToSettings,
        )
    }
}
