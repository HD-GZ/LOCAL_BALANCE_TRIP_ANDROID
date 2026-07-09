package live.lb_trip.feature.propensity

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object PropensityRoute

fun NavGraphBuilder.propensityScreen(
    onBack: () -> Unit,
) {
    composable<PropensityRoute> {
        PropensityScreen(onBack = onBack)
    }
}
