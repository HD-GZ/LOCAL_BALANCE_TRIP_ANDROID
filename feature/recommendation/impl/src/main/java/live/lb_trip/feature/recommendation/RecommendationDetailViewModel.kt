package live.lb_trip.feature.recommendation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.plus
import live.lb_trip.core.viewmodel.BaseViewModel

@HiltViewModel
class RecommendationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<RecommendationDetailUiState, RecommendationDetailSideEffect>(RecommendationDetailUiState()) {

    val courseIndex: Int = savedStateHandle.toRoute<DetailRoute>().courseIndex

    fun toggleStopExpanded(index: Int) {
        updateState {
            val isCollapsing = index in it.expandedStopIndices
            it.copy(
                expandedStopIndices = if (isCollapsing) {
                    it.expandedStopIndices - index
                } else {
                    it.expandedStopIndices + index
                },
                playingStopIndex = if (isCollapsing && it.playingStopIndex == index) null else it.playingStopIndex,
            )
        }
    }

    fun toggleAudioPlayback(stopIndex: Int) {
        updateState {
            it.copy(playingStopIndex = if (it.playingStopIndex == stopIndex) null else stopIndex)
        }
    }

    fun saveCourse() {
        updateState { it.copy(isSaved = true) }
        postSideEffect(RecommendationDetailSideEffect.ShowSaveConfirmation)
    }

    fun onTourStartClicked() {
        postSideEffect(RecommendationDetailSideEffect.ShowTourStub)
    }

    fun onIncentiveClicked() {
        postSideEffect(RecommendationDetailSideEffect.ShowIncentiveStub)
    }
}
