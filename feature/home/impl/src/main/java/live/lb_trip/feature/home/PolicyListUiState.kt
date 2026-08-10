package live.lb_trip.feature.home

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PolicyListUiState(
    val isLoading: Boolean = true,
    val cards: ImmutableList<HomeIncentiveCard> = persistentListOf(),
)

sealed interface PolicyListSideEffect {
    data class OpenUrl(val url: String) : PolicyListSideEffect
    data object ShowLoadError : PolicyListSideEffect
}
