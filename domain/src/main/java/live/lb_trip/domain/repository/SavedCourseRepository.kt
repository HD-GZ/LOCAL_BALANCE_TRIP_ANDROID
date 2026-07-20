package live.lb_trip.domain.repository

import kotlinx.coroutines.flow.Flow

interface SavedCourseRepository {
    fun observeSavedCourseIds(): Flow<Set<Long>>

    suspend fun markSaved(courseId: Long)
}
