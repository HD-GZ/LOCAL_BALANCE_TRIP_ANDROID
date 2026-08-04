package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.DistanceRecorder
import live.lb_trip.domain.repository.DistanceRecordingStore
import live.lb_trip.domain.util.suspendRunCatching
import javax.inject.Inject

class StartDistanceRecordingUseCase @Inject constructor(
    private val distanceRecorder: DistanceRecorder,
    private val distanceRecordingStore: DistanceRecordingStore,
) {
    suspend operator fun invoke(savedCourseId: Long): Result<Long> {
        val subscribed = distanceRecorder.subscribe()
        val subscribeError = subscribed.exceptionOrNull()
        if (subscribeError != null) {
            unsubscribeIfUnneeded(savedCourseId)
            return Result.failure(subscribeError)
        }

        return suspendRunCatching {
            distanceRecordingStore.getStart(savedCourseId) ?: System.currentTimeMillis().also {
                distanceRecordingStore.saveStart(savedCourseId, it)
            }
        }.onFailure { unsubscribeIfUnneeded(savedCourseId) }
    }

    private suspend fun unsubscribeIfUnneeded(savedCourseId: Long) {
        if (!distanceRecordingStore.hasOtherActiveStart(excludingSavedCourseId = savedCourseId)) {
            distanceRecorder.unsubscribe()
        }
    }
}
