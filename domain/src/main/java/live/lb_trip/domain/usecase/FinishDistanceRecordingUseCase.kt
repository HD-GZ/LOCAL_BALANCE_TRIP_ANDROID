package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.RecordedMovement
import live.lb_trip.domain.repository.DistanceRecorder
import live.lb_trip.domain.repository.DistanceRecordingStore
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class FinishDistanceRecordingUseCase @Inject constructor(
    private val distanceRecorder: DistanceRecorder,
    private val distanceRecordingStore: DistanceRecordingStore,
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(savedCourseId: Long): RecordedMovement? {
        val startedAt = distanceRecordingStore.getStart(savedCourseId)
        var movement: RecordedMovement? = null
        if (startedAt != null) {
            // Best-effort: a failed read just loses that segment, no retry.
            val result = distanceRecorder.readMovement(startedAt, System.currentTimeMillis())
            result.onSuccess {
                movement = it
                savedCourseRepository.saveTourMovement(savedCourseId, it)
            }
            distanceRecordingStore.clearStart(savedCourseId)
        }
        // The Recording API subscription is shared app-wide, not per course.
        if (!distanceRecordingStore.hasOtherActiveStart(excludingSavedCourseId = savedCourseId)) {
            distanceRecorder.unsubscribe()
        }
        return movement
    }
}
