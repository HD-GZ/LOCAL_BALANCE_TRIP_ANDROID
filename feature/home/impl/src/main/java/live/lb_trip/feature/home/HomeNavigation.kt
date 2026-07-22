package live.lb_trip.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.homeScreen(
    onStartDiagnosis: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCourseDetail: (Long) -> Unit,
    onSavedAllClick: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onStartDiagnosisClick = onStartDiagnosis,
            onMyInfoClick = onNavigateToSettings,
            onSavedAllClick = onSavedAllClick,
            onCourseClick = onNavigateToCourseDetail,
        )
    }
}
