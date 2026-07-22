package live.lb_trip.feature.recommendation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.recommendation.LbTripRecommendationException
import live.lb_trip.domain.model.CoursePlace
import live.lb_trip.domain.usecase.GetCourseDetailUseCase
import live.lb_trip.domain.usecase.SaveCourseUseCase

@HiltViewModel
class RecommendationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCourseDetailUseCase: GetCourseDetailUseCase,
    private val saveCourseUseCase: SaveCourseUseCase,
) : BaseViewModel<RecommendationDetailUiState, RecommendationDetailIntent, RecommendationDetailSideEffect>(
    RecommendationDetailUiState(),
) {

    val courseId: Long = savedStateHandle.toRoute<CourseDetailRoute>().courseId

    init {
        viewModelScope.launch { loadCourseDetail() }
    }

    override fun onIntent(intent: RecommendationDetailIntent) {
        when (intent) {
            is RecommendationDetailIntent.StopToggled -> toggleStopExpanded(intent.index)
            is RecommendationDetailIntent.PlaybackToggled -> toggleAudioPlayback(intent.stopIndex)
            RecommendationDetailIntent.SaveClicked -> saveCourse()
            RecommendationDetailIntent.TourStartClicked -> postSideEffect(RecommendationDetailSideEffect.ShowTourStub)
            RecommendationDetailIntent.IncentiveClicked -> postSideEffect(RecommendationDetailSideEffect.ShowIncentiveStub)
            RecommendationDetailIntent.Retry -> viewModelScope.launch { loadCourseDetail() }
        }
    }

    private suspend fun loadCourseDetail() {
        updateState { it.copy(isLoading = true) }
        getCourseDetailUseCase(courseId)
            .onSuccess { detail ->
                updateState {
                    it.copy(
                        isLoading = false,
                        title = detail.title,
                        stops = detail.places.map(CoursePlace::toCourseStop).toPersistentList(),
                    )
                }
                if (detail.places.isEmpty()) {
                    postSideEffect(RecommendationDetailSideEffect.ShowLoadError(DetailLoadErrorReason.EmptyPlaces))
                }
            }
            .onFailure { throwable ->
                updateState { it.copy(isLoading = false) }
                postSideEffect(RecommendationDetailSideEffect.ShowLoadError(loadErrorReasonFor(throwable)))
            }
    }

    private fun toggleStopExpanded(index: Int) {
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

    private fun toggleAudioPlayback(stopIndex: Int) {
        updateState {
            it.copy(playingStopIndex = if (it.playingStopIndex == stopIndex) null else stopIndex)
        }
    }

    private fun saveCourse() {
        if (currentState.stops.isEmpty() || currentState.isSaved || currentState.isSaving) return
        viewModelScope.launch {
            updateState { it.copy(isSaving = true) }
            saveCourseUseCase(courseId)
                .onSuccess {
                    updateState { it.copy(isSaving = false, isSaved = true) }
                    postSideEffect(RecommendationDetailSideEffect.ShowSaveConfirmation)
                }
                .onFailure {
                    updateState { it.copy(isSaving = false) }
                    postSideEffect(RecommendationDetailSideEffect.ShowSaveError)
                }
        }
    }

}

private fun CoursePlace.toCourseStop(): CourseStop = CourseStop(
    order = order,
    name = name,
    hasAudioGuide = hasAudio,
    walkDuration = walkMinutes?.let { "${it}분" },
    description = description,
    audioUrl = audioUrl,
)

private fun loadErrorReasonFor(throwable: Throwable?): DetailLoadErrorReason = when (throwable) {
    is LbTripRecommendationException.CourseNotFoundException -> DetailLoadErrorReason.CourseNotFound
    else -> DetailLoadErrorReason.Unknown
}
