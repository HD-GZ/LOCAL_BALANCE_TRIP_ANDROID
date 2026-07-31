package live.lb_trip.feature.recommendation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

data class RecommendationDetailUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val stops: ImmutableList<CourseStop> = persistentListOf(),
    val expandedStopIndices: PersistentSet<Int> = persistentSetOf(0),
    val isSaved: Boolean = false,
    val isSaving: Boolean = false,
    val playingStopIndex: Int? = null,
)

sealed interface RecommendationDetailSideEffect {
    data class ShowLoadError(val reason: DetailLoadErrorReason) : RecommendationDetailSideEffect
    data object ShowSaveConfirmation : RecommendationDetailSideEffect
    data object ShowSaveError : RecommendationDetailSideEffect
    data object ShowIncentiveStub : RecommendationDetailSideEffect
}

enum class DetailLoadErrorReason {
    CourseNotFound,
    EmptyPlaces,
    Unknown,
}
