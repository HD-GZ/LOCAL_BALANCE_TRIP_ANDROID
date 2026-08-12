package live.lb_trip.feature.tour

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class TourUiState(
    val isLoading: Boolean = true,
    val regionName: String = "",
    val title: String = "",
    val stops: ImmutableList<TourStop> = persistentListOf(),
    val benefits: ImmutableList<TourBenefit> = persistentListOf(),
    val currentStopIndex: Int = 0,
    val furthestStopIndex: Int = 0,
    val isDetailExpanded: Boolean = false,
    val tourStartedAt: String? = null,
    val elapsedMinutes: Int = 0,
    val isFinished: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val audioPositionMs: Int = 0,
    val audioDurationMs: Int = 0,
)

data class TourStop(
    val order: Int,
    val name: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val walkMinutesToNext: Int?,
    val hasAudioGuide: Boolean,
    val audioUrl: String?,
    val placeId: Long? = null,
)

data class TourBenefit(
    val title: String,
    val description: String?,
    val url: String,
)

sealed interface TourSideEffect {
    data class ShowLoadError(val reason: TourLoadErrorReason) : TourSideEffect
    data object ShowEndTourError : TourSideEffect
    data object NavigateBack : TourSideEffect
    data object CollapseSheet : TourSideEffect
    data class OpenBenefitUrl(val url: String) : TourSideEffect
}

enum class TourLoadErrorReason {
    CourseNotFound,
    EmptyPlaces,
    TourStartFailed,
    Unknown,
}
