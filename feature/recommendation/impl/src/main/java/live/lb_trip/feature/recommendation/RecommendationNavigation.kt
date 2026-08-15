package live.lb_trip.feature.recommendation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import live.lb_trip.feature.savedcourses.SavedCoursesRoute

fun NavGraphBuilder.recommendationScreen(navController: NavController, onBack: () -> Unit) {
    navigation<RecommendationRoute>(startDestination = RegionRoute) {
        composable<RegionRoute> {
            RegionScreen(
                onBack = onBack,
                onRegionSelected = { regionId, regionName ->
                    navController.navigate(CourseRoute(regionId = regionId, regionName = regionName))
                },
            )
        }
        composable<CourseRoute> {
            CourseScreen(
                onBack = navController::popBackStack,
                onCourseSelected = { courseId ->
                    navController.navigate(CourseDetailRoute(courseId = courseId))
                },
            )
        }
    }
    courseDetailScreen(navController)
}

fun NavGraphBuilder.courseDetailScreen(navController: NavController) {
    composable<CourseDetailRoute> {
        DetailScreen(
            onBack = navController::popBackStack,
            onNavigateToSavedCourses = { navController.navigate(SavedCoursesRoute()) },
        )
    }
}
