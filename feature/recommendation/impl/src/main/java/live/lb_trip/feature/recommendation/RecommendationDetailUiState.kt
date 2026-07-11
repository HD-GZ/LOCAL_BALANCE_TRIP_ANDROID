package live.lb_trip.feature.recommendation

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

data class RecommendationDetailUiState(
    val expandedStopIndices: PersistentSet<Int> = persistentSetOf(0),
    val isSaved: Boolean = false,
    val playingStopIndex: Int? = null,
)

sealed interface RecommendationDetailSideEffect {
    data object ShowSaveConfirmation : RecommendationDetailSideEffect
    data object ShowTourStub : RecommendationDetailSideEffect
    data object ShowIncentiveStub : RecommendationDetailSideEffect
}
