package live.lb_trip.domain.repository

import live.lb_trip.domain.model.RecordedMovement

interface DistanceRecorder {
    suspend fun subscribe(): Result<Unit>

    suspend fun readMovement(
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Result<RecordedMovement>

    suspend fun unsubscribe(): Result<Unit>
}
