package live.lb_trip.feature.savedcourses

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.savedCoursesScreen(onBack: () -> Unit, onCourseClick: (Long) -> Unit) {
    composable<SavedCoursesRoute> {
        SavedCoursesScreen(onBack = onBack, onCourseClick = onCourseClick)
    }
}
