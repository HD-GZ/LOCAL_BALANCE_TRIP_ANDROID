package live.lb_trip.feature.home

sealed interface PopularCourseDetailIntent {
    data class StopToggled(val index: Int) : PopularCourseDetailIntent
    data class PlaybackToggled(val stopIndex: Int) : PopularCourseDetailIntent
    data class BenefitClicked(val url: String) : PopularCourseDetailIntent
    data class NavigateClicked(val latitude: Double, val longitude: Double, val label: String) : PopularCourseDetailIntent
    data object Retry : PopularCourseDetailIntent
    data object AudioPlaybackStopRequested : PopularCourseDetailIntent
}
