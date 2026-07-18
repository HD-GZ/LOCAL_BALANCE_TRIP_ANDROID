package live.lb_trip.feature.recommendation

sealed interface CourseIntent {
    data object Retry : CourseIntent
}
