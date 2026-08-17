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
import live.lb_trip.core.util.AudioGuidePlayer
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.recommendation.LbTripRecommendationException
import live.lb_trip.domain.model.CourseBenefit
import live.lb_trip.domain.model.CoursePlace
import live.lb_trip.domain.usecase.GetCourseDetailUseCase
import live.lb_trip.domain.usecase.SaveCourseUseCase

@HiltViewModel
class RecommendationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCourseDetailUseCase: GetCourseDetailUseCase,
    private val saveCourseUseCase: SaveCourseUseCase,
    private val audioGuidePlayer: AudioGuidePlayer,
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
            is RecommendationDetailIntent.BenefitClicked ->
                postSideEffect(RecommendationDetailSideEffect.OpenBenefitUrl(intent.url))
            RecommendationDetailIntent.Retry -> viewModelScope.launch { loadCourseDetail() }
            RecommendationDetailIntent.AudioPlaybackStopRequested -> stopAudioPlayback()
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
                        stops = detail.places.toCourseStops().toPersistentList(),
                        benefits = detail.benefits.map(CourseBenefit::toRecommendationBenefit).toPersistentList(),
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
        val isCollapsing = index in currentState.expandedStopIndices
        if (isCollapsing && currentState.playingStopIndex == index) {
            audioGuidePlayer.release()
        }
        updateState {
            it.copy(
                expandedStopIndices = if (isCollapsing) {
                    it.expandedStopIndices - index
                } else {
                    it.expandedStopIndices + index
                },
                playingStopIndex = if (isCollapsing && it.playingStopIndex == index) null else it.playingStopIndex,
                isAudioPlaying = if (isCollapsing && it.playingStopIndex == index) false else it.isAudioPlaying,
            )
        }
    }

    private fun toggleAudioPlayback(stopIndex: Int) {
        val url = currentState.stops.getOrNull(stopIndex)?.audioUrl ?: return
        val isSwitchingStop = currentState.playingStopIndex != stopIndex
        updateState {
            it.copy(
                playingStopIndex = stopIndex,
                audioPositionMs = if (isSwitchingStop) 0 else it.audioPositionMs,
                audioDurationMs = if (isSwitchingStop) 0 else it.audioDurationMs,
            )
        }
        audioGuidePlayer.toggle(
            url = url,
            scope = viewModelScope,
            onState = { isPlaying, positionMs, durationMs ->
                updateState {
                    if (it.playingStopIndex == stopIndex) {
                        it.copy(isAudioPlaying = isPlaying, audioPositionMs = positionMs, audioDurationMs = durationMs)
                    } else {
                        it
                    }
                }
            },
            onCompletion = {
                updateState {
                    if (it.playingStopIndex == stopIndex) {
                        it.copy(playingStopIndex = null, isAudioPlaying = false, audioPositionMs = 0, audioDurationMs = 0)
                    } else {
                        it
                    }
                }
            },
        )
    }

    private fun stopAudioPlayback() {
        if (currentState.playingStopIndex == null) return
        audioGuidePlayer.release()
        updateState { it.copy(playingStopIndex = null, isAudioPlaying = false, audioPositionMs = 0, audioDurationMs = 0) }
    }

    override fun onCleared() {
        super.onCleared()
        audioGuidePlayer.release()
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

private fun List<CoursePlace>.toCourseStops(): List<CourseStop> = mapIndexed { index, place ->
    CourseStop(
        order = place.order,
        name = place.name,
        hasAudioGuide = place.hasAudio,
        walkDuration = getOrNull(index + 1)?.walkMinutes?.toString(),
        description = place.description,
        latitude = place.latitude,
        longitude = place.longitude,
        audioUrl = place.audioUrl,
    )
}

private fun CourseBenefit.toRecommendationBenefit(): RecommendationBenefit =
    RecommendationBenefit(title = title, description = description, url = url)

private fun loadErrorReasonFor(throwable: Throwable?): DetailLoadErrorReason = when (throwable) {
    is LbTripRecommendationException.CourseNotFoundException -> DetailLoadErrorReason.CourseNotFound
    else -> DetailLoadErrorReason.Unknown
}
