package live.lb_trip.feature.recommendation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import live.lb_trip.domain.model.RecommendedRegion

data class RegionUiState(
    val isLoading: Boolean = true,
    val regions: ImmutableList<RecommendedRegion> = persistentListOf(),
)

sealed interface RegionSideEffect {
    data class ShowError(val reason: RegionLoadErrorReason) : RegionSideEffect
}

enum class RegionLoadErrorReason {
    PropensityNotFound,
    TourApiUnavailable,
    Empty,
    Unknown,
}
