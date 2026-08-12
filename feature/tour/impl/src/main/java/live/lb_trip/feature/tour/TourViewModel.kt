package live.lb_trip.feature.tour

import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import live.lb_trip.core.util.AudioGuidePlayer
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.savedcourse.LbTripSavedCourseException
import live.lb_trip.domain.model.CourseBenefit
import live.lb_trip.domain.model.CoursePlace
import live.lb_trip.domain.model.LocationFix
import live.lb_trip.domain.model.RecordedMovement
import live.lb_trip.domain.model.TourPlaceVisit
import live.lb_trip.domain.usecase.CheckInTourPlaceUseCase
import live.lb_trip.domain.usecase.EndTourUseCase
import live.lb_trip.domain.usecase.GetSavedCourseDetailUseCase
import live.lb_trip.domain.usecase.ObserveLocationUseCase
import live.lb_trip.domain.usecase.StartTourUseCase
import live.lb_trip.domain.util.suspendRunCatching
import live.lb_trip.feature.tour.location.TourArrivalDetector

private const val ELAPSED_TICK_INTERVAL_MILLIS = 30_000L

@Suppress("TooManyFunctions", "LongParameterList")
@HiltViewModel
class TourViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSavedCourseDetailUseCase: GetSavedCourseDetailUseCase,
    private val observeLocationUseCase: ObserveLocationUseCase,
    private val startTourUseCase: StartTourUseCase,
    private val checkInTourPlaceUseCase: CheckInTourPlaceUseCase,
    private val endTourUseCase: EndTourUseCase,
    private val distanceRecording: TourDistanceRecordingCoordinator,
    private val audioGuidePlayer: AudioGuidePlayer,
) : BaseViewModel<TourUiState, TourIntent, TourSideEffect>(TourUiState()) {

    private val savedCourseId: Long = savedStateHandle.toRoute<TourRoute>().savedCourseId

    private var locationJob: Job? = null
    private var elapsedTickerJob: Job? = null
    private val arrivalDetector = TourArrivalDetector()
    private var isEndingTour = false
    private var pendingTourMovement: RecordedMovement? = null

    private var furthestStopIndex = 0

    init {
        viewModelScope.launch { loadCourseDetail() }
    }

    override fun onIntent(intent: TourIntent) {
        when (intent) {
            TourIntent.NextStopArrived -> advanceToNextStop()
            is TourIntent.StopSelected -> selectStop(intent.index)
            TourIntent.DetailExpandToggled -> updateState { it.copy(isDetailExpanded = !it.isDetailExpanded) }
            TourIntent.PlaybackToggled -> toggleAudioPlayback()
            is TourIntent.BenefitClicked -> postSideEffect(TourSideEffect.OpenBenefitUrl(intent.url))
            TourIntent.FinishAcknowledged -> postSideEffect(TourSideEffect.NavigateBack)
            TourIntent.Retry -> viewModelScope.launch { loadCourseDetail() }
            TourIntent.LocationTrackingStarted -> setLocationTracking(active = true)
            TourIntent.LocationTrackingStopped -> setLocationTracking(active = false)
            TourIntent.DistanceRecordingPermissionGranted ->
                viewModelScope.launch { distanceRecording.onPermissionGranted(savedCourseId) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioGuidePlayer.release()
    }

    private fun setLocationTracking(active: Boolean) {
        if (!active) {
            locationJob?.cancel()
            locationJob = null
            arrivalDetector.reset()
            return
        }
        if (locationJob?.isActive == true) return
        arrivalDetector.reset()
        locationJob = viewModelScope.launch {
            observeLocationUseCase()
                .catch { }
                .collect(::onLocationChanged)
        }
    }

    private fun onLocationChanged(location: LocationFix) {
        val target = currentState.stops.getOrNull(furthestStopIndex + 1) ?: return
        val arrived = arrivalDetector.onLocationChanged(
            location = location,
            targetLatitude = target.latitude,
            targetLongitude = target.longitude,
            nowElapsedRealtime = SystemClock.elapsedRealtime(),
        )
        if (arrived) {
            advanceToNextStop()
        }
    }

    private suspend fun loadCourseDetail() {
        if (isEndingTour) return
        updateState { it.copy(isLoading = true) }
        coroutineScope {
            val detailDeferred = async { getSavedCourseDetailUseCase(savedCourseId) }
            val startDeferred = async { startTourUseCase(savedCourseId) }
            val detailResult = detailDeferred.await()
            val startResult = startDeferred.await()
            val startData = startResult.getOrNull()
            val visitsByOrder = startData?.visits.orEmpty().associateBy { it.order }

            detailResult
                .onSuccess { detail ->
                    val stops = detail.places.map { it.toTourStop(visitsByOrder[it.order]?.placeId) }
                    val initialIndex = restoredStopIndex(visitsByOrder, stops.lastIndex)
                    val initialShownIndex = (initialIndex + 1).coerceAtMost(stops.lastIndex.coerceAtLeast(0))
                    furthestStopIndex = initialIndex
                    updateState {
                        it.copy(
                            isLoading = false,
                            regionName = detail.regionName,
                            title = detail.title,
                            stops = stops.toPersistentList(),
                            benefits = detail.benefits.map(CourseBenefit::toTourBenefit).toPersistentList(),
                            currentStopIndex = initialShownIndex,
                            furthestStopIndex = initialIndex,
                        )
                    }
                    arrivalDetector.reset()
                    if (detail.places.isEmpty()) {
                        postSideEffect(TourSideEffect.ShowLoadError(TourLoadErrorReason.EmptyPlaces))
                    } else {
                        if (startData != null) {
                            distanceRecording.onTourStarted(savedCourseId)
                            startElapsedTicker(startData.tourStartedAt)
                        } else {
                            postSideEffect(TourSideEffect.ShowLoadError(TourLoadErrorReason.TourStartFailed))
                        }
                        checkInStop(initialIndex)
                    }
                }
                .onFailure { throwable ->
                    updateState { it.copy(isLoading = false) }
                    postSideEffect(TourSideEffect.ShowLoadError(loadErrorReasonFor(throwable)))
                }
        }
    }

    private fun startElapsedTicker(tourStartedAt: String) {
        val startedAt = runCatching { LocalDateTime.parse(tourStartedAt) }.getOrNull() ?: return
        elapsedTickerJob?.cancel()
        elapsedTickerJob = viewModelScope.launch {
            while (isActive) {
                val minutes = Duration.between(startedAt, LocalDateTime.now()).toMinutes().coerceAtLeast(0)
                updateState { it.copy(tourStartedAt = tourStartedAt, elapsedMinutes = minutes.toInt()) }
                delay(ELAPSED_TICK_INTERVAL_MILLIS.milliseconds)
            }
        }
    }

    private fun advanceToNextStop() {
        if (currentState.isFinished) return
        val lastIndex = currentState.stops.lastIndex
        if (furthestStopIndex >= lastIndex) {
            finishTour()
            return
        }
        furthestStopIndex += 1
        stopAudioPlayback()
        updateState {
            it.copy(
                currentStopIndex = furthestStopIndex,
                furthestStopIndex = furthestStopIndex,
                isDetailExpanded = false,
            )
        }
        arrivalDetector.reset()
        checkInStop(furthestStopIndex)
        postSideEffect(TourSideEffect.CollapseSheet)
    }

    private fun selectStop(index: Int) {
        val lastIndex = currentState.stops.lastIndex
        if (index !in 0..lastIndex) return
        val didChangeSelection = index != currentState.currentStopIndex
        if (didChangeSelection) {
            stopAudioPlayback()
        }
        updateState { it.copy(currentStopIndex = index) }
        postSideEffect(TourSideEffect.CollapseSheet)
    }

    private fun checkInStop(index: Int) {
        val placeId = currentState.stops.getOrNull(index)?.placeId ?: return
        viewModelScope.launch { checkInTourPlaceUseCase(savedCourseId, placeId) }
    }

    private fun toggleAudioPlayback() {
        val url = currentState.stops.getOrNull(currentState.currentStopIndex)?.audioUrl ?: return
        audioGuidePlayer.toggle(
            url = url,
            scope = viewModelScope,
            onState = { isPlaying, positionMs, durationMs ->
                updateState { it.copy(isAudioPlaying = isPlaying, audioPositionMs = positionMs, audioDurationMs = durationMs) }
            },
            onCompletion = {
                updateState { it.copy(isAudioPlaying = false, audioPositionMs = 0, audioDurationMs = 0) }
            },
        )
    }

    private fun stopAudioPlayback() {
        if (!currentState.isAudioPlaying) return
        audioGuidePlayer.release()
        updateState { it.copy(isAudioPlaying = false, audioPositionMs = 0, audioDurationMs = 0) }
    }

    private fun finishTour() {
        if (isEndingTour) return
        isEndingTour = true
        viewModelScope.launch {
            // finish() clears the distance-recording session, so it must only run once;
            // cache the result so a retry after an endTour failure resends the same distance.
            val movement = pendingTourMovement ?: withContext(NonCancellable) {
                suspendRunCatching { distanceRecording.finish(savedCourseId) }.getOrNull()
            }.also { pendingTourMovement = it }
            val endTourResult = withContext(NonCancellable) {
                endTourUseCase(savedCourseId, movement?.distanceMeters)
            }
            isEndingTour = false
            if (endTourResult.isFailure) {
                postSideEffect(TourSideEffect.ShowEndTourError)
                return@launch
            }
            elapsedTickerJob?.cancel()
            val durationMinutes = endTourResult.getOrNull()?.durationMinutes
            updateState {
                it.copy(
                    isFinished = true,
                    currentStopIndex = furthestStopIndex,
                    elapsedMinutes = durationMinutes?.toInt() ?: it.elapsedMinutes,
                )
            }
        }
    }
}

internal fun restoredStopIndex(visitsByOrder: Map<Int, TourPlaceVisit>, lastIndex: Int): Int {
    if (visitsByOrder.isEmpty()) return 0
    val firstUnvisitedOrder = visitsByOrder.values.filterNot { it.visited }.minOfOrNull { it.order }
    val index = firstUnvisitedOrder?.let { it - 1 } ?: lastIndex
    return index.coerceIn(0, lastIndex)
}

private fun CoursePlace.toTourStop(placeId: Long?): TourStop = TourStop(
    order = order,
    name = name,
    description = description,
    latitude = latitude,
    longitude = longitude,
    walkMinutesToNext = walkMinutes,
    hasAudioGuide = hasAudio,
    audioUrl = audioUrl,
    placeId = placeId,
)

private fun CourseBenefit.toTourBenefit(): TourBenefit = TourBenefit(title = title, description = description, url = url)

private fun loadErrorReasonFor(throwable: Throwable?): TourLoadErrorReason = when (throwable) {
    is LbTripSavedCourseException.SavedCourseNotFoundException -> TourLoadErrorReason.CourseNotFound
    else -> TourLoadErrorReason.Unknown
}

