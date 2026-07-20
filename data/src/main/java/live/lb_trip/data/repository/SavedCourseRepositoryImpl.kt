package live.lb_trip.data.repository

import kotlinx.coroutines.flow.Flow
import live.lb_trip.data.datasource.local.SavedCourseDataStore
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class SavedCourseRepositoryImpl @Inject constructor(
    private val savedCourseDataStore: SavedCourseDataStore,
) : SavedCourseRepository {

    override fun observeSavedCourseIds(): Flow<Set<Long>> = savedCourseDataStore.savedCourseIds

    override suspend fun markSaved(courseId: Long) = savedCourseDataStore.markSaved(courseId)
}
