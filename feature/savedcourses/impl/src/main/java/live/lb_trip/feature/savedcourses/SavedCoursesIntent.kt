package live.lb_trip.feature.savedcourses

sealed interface SavedCoursesIntent {
    data object Retry : SavedCoursesIntent
    data object Refresh : SavedCoursesIntent
}
