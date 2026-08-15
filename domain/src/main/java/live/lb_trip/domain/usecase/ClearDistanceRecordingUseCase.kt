package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.DistanceRecorder
import live.lb_trip.domain.repository.DistanceRecordingStore
import javax.inject.Inject

class ClearDistanceRecordingUseCase @Inject constructor(
    private val distanceRecorder: DistanceRecorder,
    private val distanceRecordingStore: DistanceRecordingStore,
) {
    suspend operator fun invoke() {
        distanceRecorder.unsubscribe()
        distanceRecordingStore.clearAll()
    }
}
