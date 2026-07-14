package live.lb_trip.feature.recommendation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.recommendation.LbTripRecommendationException
import live.lb_trip.domain.usecase.CreateRecommendationsUseCase
import live.lb_trip.domain.usecase.GetRecommendedRegionsUseCase

@HiltViewModel
class RegionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val createRecommendationsUseCase: CreateRecommendationsUseCase,
    private val getRecommendedRegionsUseCase: GetRecommendedRegionsUseCase,
) : BaseViewModel<RegionUiState, RegionIntent, RegionSideEffect>(RegionUiState()) {

    init {
        viewModelScope.launch { loadRegions() }
    }

    override fun onIntent(intent: RegionIntent) {
        when (intent) {
            RegionIntent.Retry -> viewModelScope.launch { loadRegions() }
        }
    }

    private suspend fun loadRegions() {
        updateState { it.copy(isLoading = true) }

        if (savedStateHandle.get<Boolean>(HAS_CREATED_RECOMMENDATIONS_KEY) != true) {
            val createResult = createRecommendationsUseCase()
            if (createResult.isFailure) {
                updateState { it.copy(isLoading = false) }
                postSideEffect(RegionSideEffect.ShowError(loadErrorReasonFor(createResult.exceptionOrNull())))
                return
            }
            savedStateHandle[HAS_CREATED_RECOMMENDATIONS_KEY] = true
        }

        getRecommendedRegionsUseCase()
            .onSuccess { regions ->
                updateState { it.copy(isLoading = false, regions = regions.toPersistentList()) }
                if (regions.isEmpty()) postSideEffect(RegionSideEffect.ShowError(RegionLoadErrorReason.Empty))
            }
            .onFailure { throwable ->
                updateState { it.copy(isLoading = false) }
                postSideEffect(RegionSideEffect.ShowError(loadErrorReasonFor(throwable)))
            }
    }
}

private const val HAS_CREATED_RECOMMENDATIONS_KEY = "hasCreatedRecommendations"

private fun loadErrorReasonFor(throwable: Throwable?): RegionLoadErrorReason = when (throwable) {
    is LbTripRecommendationException.PropensityNotFoundException -> RegionLoadErrorReason.PropensityNotFound
    is LbTripRecommendationException.TourApiUnavailableException -> RegionLoadErrorReason.TourApiUnavailable
    else -> RegionLoadErrorReason.Unknown
}
