package live.lb_trip.feature.savedcourses

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import live.lb_trip.core.util.AudioGuidePlayer
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.savedcourse.LbTripSavedCourseException
import live.lb_trip.domain.model.CourseBenefit
import live.lb_trip.domain.model.CoursePlace
import live.lb_trip.domain.usecase.GetSharedCourseDetailUseCase

@HiltViewModel(assistedFactory = SharedCourseDetailViewModel.Factory::class)
class SharedCourseDetailViewModel @AssistedInject constructor(
    @Assisted private val token: String,
    private val getSharedCourseDetailUseCase: GetSharedCourseDetailUseCase,
    private val audioGuidePlayer: AudioGuidePlayer,
) : BaseViewModel<SharedCourseDetailUiState, SharedCourseDetailIntent, SharedCourseDetailSideEffect>(
    SharedCourseDetailUiState(),
) {

    @AssistedFactory
    interface Factory {
        fun create(token: String): SharedCourseDetailViewModel
    }

    init {
        viewModelScope.launch { load() }
    }

    override fun onIntent(intent: SharedCourseDetailIntent) {
        when (intent) {
            is SharedCourseDetailIntent.StopToggled -> toggleStopExpanded(intent.index)
            is SharedCourseDetailIntent.PlaybackToggled -> togglePlayback(intent.stopIndex)
            is SharedCourseDetailIntent.BenefitClicked ->
                postSideEffect(SharedCourseDetailSideEffect.OpenBenefitUrl(intent.url))
            is SharedCourseDetailIntent.NavigateClicked ->
                postSideEffect(SharedCourseDetailSideEffect.OpenMap(intent.latitude, intent.longitude, intent.label))
            SharedCourseDetailIntent.Retry -> viewModelScope.launch { load() }
        }
    }

    private suspend fun load() {
        updateState { it.copy(isLoading = true, loadFailed = false) }
        getSharedCourseDetailUseCase(token)
            .onSuccess { detail ->
                updateState {
                    it.copy(
                        isLoading = false,
                        sharedByName = detail.sharedByName,
                        imageUrl = detail.imageUrl,
                        regionName = detail.regionName,
                        title = detail.title,
                        status = detail.status,
                        stops = detail.places.toSharedCourseStops().toPersistentList(),
                        walkTotalMinutes = detail.places.sumOf { place -> place.walkMinutes ?: 0 },
                        audioGuideCount = detail.places.count { place -> place.hasAudio },
                        benefits = detail.benefits.map(CourseBenefit::toSharedCourseBenefit).toPersistentList(),
                    )
                }
            }
            .onFailure { throwable ->
                updateState { it.copy(isLoading = false, loadFailed = true) }
                postSideEffect(SharedCourseDetailSideEffect.ShowLoadError(loadErrorReasonFor(throwable)))
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

    private fun togglePlayback(stopIndex: Int) {
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

    override fun onCleared() {
        super.onCleared()
        audioGuidePlayer.release()
    }
}

private fun List<CoursePlace>.toSharedCourseStops(): List<SharedCourseStop> = mapIndexed { index, place ->
    SharedCourseStop(
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

private fun CourseBenefit.toSharedCourseBenefit(): SavedCourseBenefit =
    SavedCourseBenefit(title = title, description = description, url = url)

private fun loadErrorReasonFor(throwable: Throwable?): SharedCourseDetailLoadErrorReason = when (throwable) {
    is LbTripSavedCourseException.ShareTokenNotFoundException -> SharedCourseDetailLoadErrorReason.TokenNotFound
    is LbTripSavedCourseException.ShareTokenExpiredException -> SharedCourseDetailLoadErrorReason.TokenExpired
    else -> SharedCourseDetailLoadErrorReason.Unknown
}
