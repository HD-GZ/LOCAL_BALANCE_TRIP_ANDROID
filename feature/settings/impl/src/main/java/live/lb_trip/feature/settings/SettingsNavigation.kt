package live.lb_trip.feature.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.settingsScreen(onNavigateToMain: () -> Unit, onNavigateToSavedCourses: () -> Unit) {
    composable<SettingsRoute> {
        SettingsScreen(
            onNavigateToMain = onNavigateToMain,
            onNavigateToSavedCourses = onNavigateToSavedCourses,
        )
    }
}
