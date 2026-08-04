package live.lb_trip.domain.repository

interface DistanceRecordingStore {
    suspend fun getStart(savedCourseId: Long): Long?

    suspend fun saveStart(
        savedCourseId: Long,
        startEpochMillis: Long,
    )

    suspend fun clearStart(savedCourseId: Long)

    suspend fun hasOtherActiveStart(excludingSavedCourseId: Long): Boolean

    /** Wipes all locally persisted tour distance/step data -- recording sessions and saved report values alike. */
    suspend fun clearAll()
}
