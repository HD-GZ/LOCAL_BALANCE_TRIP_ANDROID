package live.lb_trip.feature.tour

import javax.inject.Inject
import live.lb_trip.domain.model.RecordedMovement
import live.lb_trip.domain.usecase.FinishDistanceRecordingUseCase
import live.lb_trip.domain.usecase.StartDistanceRecordingUseCase

/** Tour-session-scoped (not screen-visibility-scoped) distance recording, gated on "tour started + permission granted". */
class TourDistanceRecordingCoordinator @Inject constructor(
    private val startDistanceRecordingUseCase: StartDistanceRecordingUseCase,
    private val finishDistanceRecordingUseCase: FinishDistanceRecordingUseCase,
) {
    private var tourStarted = false
    private var hasPermission = false
    private var hasStarted = false

    suspend fun onTourStarted(savedCourseId: Long) {
        tourStarted = true
        startIfNeeded(savedCourseId)
    }

    suspend fun onPermissionGranted(savedCourseId: Long) {
        hasPermission = true
        startIfNeeded(savedCourseId)
    }

    suspend fun finish(savedCourseId: Long): RecordedMovement? = finishDistanceRecordingUseCase(savedCourseId)

    private suspend fun startIfNeeded(savedCourseId: Long) {
        if (hasStarted || !tourStarted || !hasPermission) return
        startDistanceRecordingUseCase(savedCourseId).onSuccess { hasStarted = true }
    }
}
