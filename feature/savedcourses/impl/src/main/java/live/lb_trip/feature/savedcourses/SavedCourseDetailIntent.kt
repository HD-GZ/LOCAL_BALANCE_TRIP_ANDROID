package live.lb_trip.feature.savedcourses

sealed interface SavedCourseDetailIntent {
    data class TabSelected(val tab: SavedCourseDetailTab) : SavedCourseDetailIntent
    data class StopToggled(val index: Int) : SavedCourseDetailIntent
    data class BenefitClicked(val url: String) : SavedCourseDetailIntent
    data object TourStartClicked : SavedCourseDetailIntent
    data object Retry : SavedCourseDetailIntent
    data object ReceiptRegistered : SavedCourseDetailIntent
    data class KakaoShareClicked(val title: String, val description: String, val imageUrl: String?) : SavedCourseDetailIntent
}
