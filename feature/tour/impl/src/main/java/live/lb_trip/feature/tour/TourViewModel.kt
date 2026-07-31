package live.lb_trip.feature.tour

import android.location.Location
import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
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

@HiltViewModel
class TourViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSavedCourseDetailUseCase: GetSavedCourseDetailUseCase,
    private val observeLocationUseCase: ObserveLocationUseCase,
    private val startTourUseCase: StartTourUseCase,
    private val checkInTourPlaceUseCase: CheckInTourPlaceUseCase,
    private val endTourUseCase: EndTourUseCase,
) : BaseViewModel<TourUiState, TourIntent, TourSideEffect>(TourUiState()) {

    private val savedCourseId: Long = savedStateHandle.toRoute<TourRoute>().savedCourseId

    private var locationJob: Job? = null
    private var inRadiusSinceElapsedRealtime: Long? = null
    private var previousInRadiusDistanceMeters: Float? = null
    private var lastAcceptedFixElapsedRealtime: Long? = null

    // Consecutive stops can be closer together than the arrival radius. Requiring a fix
    // outside the current target's radius before it can auto-arrive (instead of only rate
    // limiting with a cooldown) is what actually stops one GPS fix from walking the whole
    // course forward -- if two real stops are genuinely this close, the second one just
    // needs a manual tap.
    private var isArmedForCurrentTarget = false

    init {
        viewModelScope.launch { loadCourseDetail() }
    }

    override fun onIntent(intent: TourIntent) {
        when (intent) {
            TourIntent.NextStopArrived -> advanceToNextStop()
            is TourIntent.StopSelected -> selectStop(intent.index)
            TourIntent.EndTourClicked -> {
                // Fire-and-forget: ending the tour is best-effort bookkeeping, not something the
                // user should have to wait on a network round trip for before leaving the screen.
                viewModelScope.launch { endTourUseCase(savedCourseId) }
                postSideEffect(TourSideEffect.NavigateBack)
            }
            TourIntent.Retry -> viewModelScope.launch { loadCourseDetail() }
            TourIntent.LocationTrackingStarted -> startLocationTracking()
            TourIntent.LocationTrackingStopped -> stopLocationTracking()
        }
    }

    private fun startLocationTracking() {
        if (locationJob?.isActive == true) return
        // Only the dwell clock restarts here, not the armed flag -- a background/foreground
        // cycle must not re-require leaving the target's radius, or a user who backgrounds
        // the app while walking and reopens it already standing at the target would never
        // auto-arrive (armed was already earned during this same approach).
        resetDwellClock()
        locationJob = viewModelScope.launch {
            observeLocationUseCase()
                .catch { /* auto-arrival is best-effort; the manual button still works */ }
                .collect(::onLocationChanged)
        }
    }

    private fun stopLocationTracking() {
        locationJob?.cancel()
        locationJob = null
        resetDwellClock()
    }

    private fun onLocationChanged(location: LocationFix) {
        // Emulator-injected fixes can arrive with no accuracy value at all -- a null check
        // must gate the comparison, or every spoofed fix is silently dropped.
        val accuracy = location.accuracyMeters
        if (accuracy != null && accuracy > MAX_ACCURACY_METERS) return
        if (location.ageMillis > MAX_LOCATION_AGE_MILLIS) return

        val stops = currentState.stops
        val index = currentState.currentStopIndex
        // currentStopIndex is the stop already reached; the physical destination the user
        // is walking toward is the next one. On the last stop there is no next target, so
        // getOrNull naturally disables auto-arrival there -- finishing the tour stays manual.
        val target = stops.getOrNull(index + 1) ?: return

        val distance = distanceMeters(location.latitude, location.longitude, target.latitude, target.longitude)
        val now = SystemClock.elapsedRealtime()

        // A gap this long between accepted fixes (e.g. GPS degraded and fixes were dropped
        // by the accuracy/age gates above) means the dwell clock can no longer vouch for
        // continuous presence -- restart it rather than let a stale start time grant an
        // instant arrival once fixes resume.
        val lastAccepted = lastAcceptedFixElapsedRealtime
        if (lastAccepted != null && now - lastAccepted > MAX_FIX_GAP_MILLIS) resetDwellClock()
        lastAcceptedFixElapsedRealtime = now

        // `distance <= RADIUS` (rather than `distance > RADIUS`) also treats a NaN distance
        // (malformed lat/lon) as out-of-radius instead of as "standing on the stop".
        if (!(distance <= ARRIVAL_RADIUS_METERS)) {
            resetDwellClock()
            isArmedForCurrentTarget = true
            return
        }
        if (!isArmedForCurrentTarget) return

        val since = inRadiusSinceElapsedRealtime ?: now.also { inRadiusSinceElapsedRealtime = it }
        // Required dwell shrinks the closer the fix is: right at the radius edge (1km)
        // still needs the full confirmation window, but a fix right on top of the stop is
        // much stronger evidence of a real arrival and shouldn't need as long. Corroborating
        // against the previous accepted fix (rather than trusting the latest fix alone)
        // means a single spurious close fix -- a multipath jump -- can't by itself cut a 30s
        // wait down to the 5s floor; two fixes in a row must agree the user is this close.
        val corroboratedDistance = maxOf(previousInRadiusDistanceMeters ?: distance, distance)
        previousInRadiusDistanceMeters = distance
        if (now - since < dwellMillisFor(corroboratedDistance)) return

        advanceToNextStop()
    }

    private fun resetDwellClock() {
        inRadiusSinceElapsedRealtime = null
        previousInRadiusDistanceMeters = null
    }

    /** Only an actual target change (manual or automatic) disarms -- see [startLocationTracking]. */
    private fun resetProximityTracking() {
        resetDwellClock()
        isArmedForCurrentTarget = false
    }

    private suspend fun loadCourseDetail() {
        updateState { it.copy(isLoading = true) }
        coroutineScope {
            val detailDeferred = async { getSavedCourseDetailUseCase(savedCourseId) }
            // Best-effort: transitions the course to TRAVELING server-side and returns each
            // place's real placeId + visited flag, needed for check-in and to resume progress
            // on a restarted tour. If this fails, the tour still works locally -- it just can't
            // report accurate visit data to the backend.
            val startDeferred = async { startTourUseCase(savedCourseId) }
            val detailResult = detailDeferred.await()
            val visitsByOrder = startDeferred.await().getOrNull().orEmpty().associateBy { it.order }

            detailResult
                .onSuccess { detail ->
                    val stops = detail.places.map { it.toTourStop(visitsByOrder[it.order]?.placeId) }
                    updateState {
                        it.copy(
                            isLoading = false,
                            regionName = detail.regionName,
                            title = detail.title,
                            stops = stops.toPersistentList(),
                            currentStopIndex = restoredStopIndex(visitsByOrder, stops.lastIndex),
                        )
                    }
                    resetProximityTracking()
                    if (detail.places.isEmpty()) {
                        postSideEffect(TourSideEffect.ShowLoadError(TourLoadErrorReason.EmptyPlaces))
                    } else {
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
        if (currentState.currentStopIndex >= lastIndex) {
            viewModelScope.launch { endTourUseCase(savedCourseId) }
            postSideEffect(TourSideEffect.NavigateBack)
            return
        }
        updateState { it.copy(currentStopIndex = it.currentStopIndex + 1) }
        resetProximityTracking()
        checkInCurrentStop()
        postSideEffect(TourSideEffect.CollapseSheet)
    }

    private fun selectStop(index: Int) {
        val lastIndex = currentState.stops.lastIndex
        if (index !in 0..lastIndex) return
        // Tapping the currently-selected stop's own marker must not disarm -- see
        // [resetProximityTracking]. Only a real target change should reset it.
        val didChangeTarget = index != currentState.currentStopIndex
        updateState { it.copy(currentStopIndex = index) }
        if (didChangeTarget) {
            resetProximityTracking()
            checkInCurrentStop()
        }
        postSideEffect(TourSideEffect.CollapseSheet)
    }

    private fun checkInCurrentStop() {
        val placeId = currentState.stops.getOrNull(currentState.currentStopIndex)?.placeId ?: return
        viewModelScope.launch { checkInTourPlaceUseCase(savedCourseId, placeId) }
    }
}

/** Resumes a restarted tour at its first unvisited stop; falls back to the first stop when no visit data is available. */
private fun restoredStopIndex(visitsByOrder: Map<Int, TourPlaceVisit>, lastIndex: Int): Int {
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

private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val results = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    return results[0]
}

/** Linearly interpolates from [MAX_DWELL_MILLIS] at the radius edge down to [MIN_DWELL_MILLIS] at the stop itself. */
private fun dwellMillisFor(distanceMeters: Float): Long {
    val farnessFraction = (distanceMeters / ARRIVAL_RADIUS_METERS).coerceIn(0f, 1f)
    return MIN_DWELL_MILLIS + (farnessFraction * (MAX_DWELL_MILLIS - MIN_DWELL_MILLIS)).toLong()
}

private const val ARRIVAL_RADIUS_METERS = 1_000f
private const val MAX_DWELL_MILLIS = 30_000L
private const val MIN_DWELL_MILLIS = 5_000L
private const val MAX_ACCURACY_METERS = 150f
private const val MAX_LOCATION_AGE_MILLIS = 60_000L
private const val MAX_FIX_GAP_MILLIS = 20_000L
