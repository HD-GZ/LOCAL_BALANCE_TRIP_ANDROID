package live.lb_trip.feature.tour

import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.savedcourse.LbTripSavedCourseException
import live.lb_trip.domain.model.CoursePlace
import live.lb_trip.domain.model.LocationFix
import live.lb_trip.domain.model.TourPlaceVisit
import live.lb_trip.domain.usecase.CheckInTourPlaceUseCase
import live.lb_trip.domain.usecase.EndTourUseCase
import live.lb_trip.domain.usecase.GetSavedCourseDetailUseCase
import live.lb_trip.domain.usecase.ObserveLocationUseCase
import live.lb_trip.domain.usecase.StartTourUseCase
import live.lb_trip.domain.util.suspendRunCatching
import live.lb_trip.feature.tour.location.TourArrivalDetector

@HiltViewModel
class TourViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSavedCourseDetailUseCase: GetSavedCourseDetailUseCase,
    private val observeLocationUseCase: ObserveLocationUseCase,
    private val startTourUseCase: StartTourUseCase,
    private val checkInTourPlaceUseCase: CheckInTourPlaceUseCase,
    private val endTourUseCase: EndTourUseCase,
    private val distanceRecording: TourDistanceRecordingCoordinator,
) : BaseViewModel<TourUiState, TourIntent, TourSideEffect>(TourUiState()) {

    private val savedCourseId: Long = savedStateHandle.toRoute<TourRoute>().savedCourseId

    private var locationJob: Job? = null
    private val arrivalDetector = TourArrivalDetector()
    private var isEndingTour = false

    private var furthestStopIndex = 0

    init {
        viewModelScope.launch { loadCourseDetail() }
    }

    override fun onIntent(intent: TourIntent) {
        when (intent) {
            TourIntent.NextStopArrived -> advanceToNextStop()
            is TourIntent.StopSelected -> selectStop(intent.index)
            TourIntent.EndTourClicked -> endTourAndNavigateBack()
            TourIntent.Retry -> viewModelScope.launch { loadCourseDetail() }
            TourIntent.LocationTrackingStarted -> setLocationTracking(active = true)
            TourIntent.LocationTrackingStopped -> setLocationTracking(active = false)
            TourIntent.DistanceRecordingPermissionGranted ->
                viewModelScope.launch { distanceRecording.onPermissionGranted(savedCourseId) }
        }
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
            val visitsByOrder = startResult.getOrNull().orEmpty().associateBy { it.order }

            detailResult
                .onSuccess { detail ->
                    val stops = detail.places.map { it.toTourStop(visitsByOrder[it.order]?.placeId) }
                    val initialIndex = restoredStopIndex(visitsByOrder, stops.lastIndex)
                    furthestStopIndex = initialIndex
                    updateState {
                        it.copy(
                            isLoading = false,
                            regionName = detail.regionName,
                            title = detail.title,
                            stops = stops.toPersistentList(),
                            currentStopIndex = initialIndex,
                            furthestStopIndex = initialIndex,
                        )
                    }
                    arrivalDetector.reset()
                    if (detail.places.isEmpty()) {
                        postSideEffect(TourSideEffect.ShowLoadError(TourLoadErrorReason.EmptyPlaces))
                    } else {
                        if (startResult.isSuccess) {
                            distanceRecording.onTourStarted(savedCourseId)
                        } else {
                            postSideEffect(TourSideEffect.ShowLoadError(TourLoadErrorReason.TourStartFailed))
                        }
                        checkInCurrentStop()
                    }
                }
                .onFailure { throwable ->
                    updateState { it.copy(isLoading = false) }
                    postSideEffect(TourSideEffect.ShowLoadError(loadErrorReasonFor(throwable)))
                }
        }
    }

    private fun advanceToNextStop() {
        val lastIndex = currentState.stops.lastIndex
        if (furthestStopIndex >= lastIndex) {
            endTourAndNavigateBack()
            return
        }
        furthestStopIndex += 1
        updateState { it.copy(currentStopIndex = furthestStopIndex, furthestStopIndex = furthestStopIndex) }
        arrivalDetector.reset()
        checkInCurrentStop()
        postSideEffect(TourSideEffect.CollapseSheet)
    }

    private fun selectStop(index: Int) {
        val lastIndex = currentState.stops.lastIndex
        if (index !in 0..lastIndex) return
        val didChangeSelection = index != currentState.currentStopIndex
        updateState { it.copy(currentStopIndex = index) }
        if (didChangeSelection) {
            checkInCurrentStop()
        }
        postSideEffect(TourSideEffect.CollapseSheet)
    }

    private fun checkInCurrentStop() {
        val placeId = currentState.stops.getOrNull(currentState.currentStopIndex)?.placeId ?: return
        viewModelScope.launch { checkInTourPlaceUseCase(savedCourseId, placeId) }
    }

    private fun endTourAndNavigateBack() {
        if (isEndingTour) return
        isEndingTour = true
        viewModelScope.launch {
            val endTourResult = withContext(NonCancellable) { endTourUseCase(savedCourseId) }
            if (endTourResult.isFailure) {
                isEndingTour = false
                postSideEffect(TourSideEffect.ShowEndTourError)
                return@launch
            }
            withContext(NonCancellable) { suspendRunCatching { distanceRecording.finish(savedCourseId) } }
            postSideEffect(TourSideEffect.NavigateBack)
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
    latitude = latitude,
    longitude = longitude,
    walkMinutesToNext = walkMinutes,
    placeId = placeId,
)

private fun loadErrorReasonFor(throwable: Throwable?): TourLoadErrorReason = when (throwable) {
    is LbTripSavedCourseException.SavedCourseNotFoundException -> TourLoadErrorReason.CourseNotFound
    else -> TourLoadErrorReason.Unknown
}

