package live.lb_trip.feature.home

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
import live.lb_trip.domain.exception.home.LbTripHomeException
import live.lb_trip.domain.model.CoursePlace
import live.lb_trip.domain.usecase.GetPopularCourseDetailUseCase

@HiltViewModel
class PopularCourseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPopularCourseDetailUseCase: GetPopularCourseDetailUseCase,
    private val audioGuidePlayer: AudioGuidePlayer,
) : BaseViewModel<PopularCourseDetailUiState, PopularCourseDetailIntent, PopularCourseDetailSideEffect>(
    PopularCourseDetailUiState(),
) {

    private val courseId: Long = savedStateHandle.toRoute<PopularCourseDetailRoute>().courseId

    init {
        viewModelScope.launch { loadCourseDetail() }
    }

    override fun onIntent(intent: PopularCourseDetailIntent) {
        when (intent) {
            is PopularCourseDetailIntent.StopToggled -> toggleStopExpanded(intent.index)
            is PopularCourseDetailIntent.PlaybackToggled -> toggleAudioPlayback(intent.stopIndex)
            is PopularCourseDetailIntent.BenefitClicked ->
                postSideEffect(PopularCourseDetailSideEffect.OpenBenefitUrl(intent.url))
            is PopularCourseDetailIntent.NavigateClicked ->
                postSideEffect(PopularCourseDetailSideEffect.OpenMap(intent.latitude, intent.longitude, intent.label))
            PopularCourseDetailIntent.Retry -> viewModelScope.launch { loadCourseDetail() }
            PopularCourseDetailIntent.AudioPlaybackStopRequested -> stopAudioPlayback()
        }
    }

    private suspend fun loadCourseDetail() {
        updateState { it.copy(isLoading = true) }
        getPopularCourseDetailUseCase(courseId)
            .onSuccess { detail ->
                updateState {
                    it.copy(
                        isLoading = false,
                        regionName = detail.regionName,
                        title = detail.title,
                        stops = detail.places.toPopularCourseStops().toPersistentList(),
                        benefits = detail.benefits.toPersistentList(),
                        walkTotalMinutes = detail.places.sumOf { place -> place.walkMinutes ?: 0 },
                    )
                }
                if (detail.places.isEmpty()) {
                    postSideEffect(
                        PopularCourseDetailSideEffect.ShowLoadError(PopularCourseDetailLoadErrorReason.EmptyPlaces),
                    )
                }
            }
            .onFailure { throwable ->
                updateState { it.copy(isLoading = false) }
                postSideEffect(PopularCourseDetailSideEffect.ShowLoadError(loadErrorReasonFor(throwable)))
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
}

private fun List<CoursePlace>.toPopularCourseStops(): List<PopularCourseStop> = mapIndexed { index, place ->
    PopularCourseStop(
        order = place.order,
        name = place.name,
        description = place.description,
        imageUrl = place.imageUrl,
        latitude = place.latitude,
        longitude = place.longitude,
        walkToNextMinutes = getOrNull(index + 1)?.walkMinutes,
        hasAudioGuide = place.hasAudio,
        audioUrl = place.audioUrl,
    )
}

private fun loadErrorReasonFor(throwable: Throwable?): PopularCourseDetailLoadErrorReason = when (throwable) {
    is LbTripHomeException.CourseNotFoundException -> PopularCourseDetailLoadErrorReason.CourseNotFound
    else -> PopularCourseDetailLoadErrorReason.Unknown
}
