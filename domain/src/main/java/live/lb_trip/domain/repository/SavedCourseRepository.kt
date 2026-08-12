package live.lb_trip.domain.repository

import live.lb_trip.domain.model.RecordedMovement
import live.lb_trip.domain.model.SavedCourseDetail
import live.lb_trip.domain.model.SavedCourseList
import live.lb_trip.domain.model.SavedCourseReport
import live.lb_trip.domain.model.ShareToken
import live.lb_trip.domain.model.SharedCourseDetail
import live.lb_trip.domain.model.TourPlaceVisit

interface SavedCourseRepository {
    suspend fun getSavedCourses(): Result<SavedCourseList>

    suspend fun getSavedCourseDetail(savedCourseId: Long): Result<SavedCourseDetail>

    suspend fun issueShareToken(savedCourseId: Long): Result<ShareToken>

    suspend fun getSharedCourseDetail(token: String): Result<SharedCourseDetail>

    suspend fun getSavedCourseReport(savedCourseId: Long): Result<SavedCourseReport>

    suspend fun startTour(savedCourseId: Long): Result<List<TourPlaceVisit>>

    suspend fun checkInTourPlace(
        savedCourseId: Long,
        placeId: Long,
    ): Result<Unit>

    suspend fun endTour(
        savedCourseId: Long,
        distanceMeters: Float?,
    ): Result<Unit>

    suspend fun saveTourMovement(
        savedCourseId: Long,
        movement: RecordedMovement,
    )
}
