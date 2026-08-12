package live.lb_trip.feature.savedcourses

sealed interface SharedCourseDetailIntent {
    data class StopToggled(val index: Int) : SharedCourseDetailIntent
    data class PlaybackToggled(val stopIndex: Int) : SharedCourseDetailIntent
    data class BenefitClicked(val url: String) : SharedCourseDetailIntent
    data class NavigateClicked(val latitude: Double, val longitude: Double, val label: String) : SharedCourseDetailIntent
    data object Retry : SharedCourseDetailIntent
}
