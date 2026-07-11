package live.lb_trip.feature.recommendation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute

fun NavGraphBuilder.recommendationScreen(
    navController: NavController,
    onBack: () -> Unit,
) {
    navigation<RecommendationRoute>(startDestination = RegionRoute) {
        composable<RegionRoute> {
            RegionScreen(
                onBack = onBack,
                onRegionSelected = { index -> navController.navigate(CourseRoute(regionIndex = index)) },
            )
        }
        composable<CourseRoute> { backStackEntry ->
            val route: CourseRoute = backStackEntry.toRoute()
            CourseScreen(
                regionIndex = route.regionIndex,
                onBack = navController::popBackStack,
                onCourseSelected = { index ->
                    navController.navigate(DetailRoute(courseIndex = index))
                },
            )
        }
        composable<DetailRoute> {
            DetailScreen(onBack = navController::popBackStack)
        }
    }
}
