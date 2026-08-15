package live.lb_trip.feature.recommendation

sealed interface RecommendationDetailIntent {
    data class StopToggled(val index: Int) : RecommendationDetailIntent
    data class PlaybackToggled(val stopIndex: Int) : RecommendationDetailIntent
    data object SaveClicked : RecommendationDetailIntent
    data class BenefitClicked(val url: String) : RecommendationDetailIntent
    data object Retry : RecommendationDetailIntent
    data object AudioPlaybackStopRequested : RecommendationDetailIntent
}
