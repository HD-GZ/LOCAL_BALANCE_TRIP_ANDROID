package live.lb_trip.feature.tour

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class TourUiState(
    val isLoading: Boolean = true,
    val regionName: String = "",
    val title: String = "",
    val stops: ImmutableList<TourStop> = persistentListOf(),
    val currentStopIndex: Int = 0,
)

data class TourStop(
    val order: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val walkMinutesToNext: Int?,
)

sealed interface TourSideEffect {
    data class ShowLoadError(val reason: TourLoadErrorReason) : TourSideEffect
    data object NavigateBack : TourSideEffect
    data object CollapseSheet : TourSideEffect
}

enum class TourLoadErrorReason {
    CourseNotFound,
    EmptyPlaces,
    Unknown,
}
