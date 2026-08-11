package live.lb_trip.data.repository

import live.lb_trip.data.datasource.local.SavedCourseDistanceLocalDataSource
import live.lb_trip.data.datasource.remote.SavedCourseRemoteDataSource
import live.lb_trip.data.mapper.toDomain
import live.lb_trip.domain.exception.savedcourse.LbTripSavedCourseException
import live.lb_trip.domain.model.RecordedMovement
import live.lb_trip.domain.model.SavedCourseDetail
import live.lb_trip.domain.model.SavedCourseList
import live.lb_trip.domain.model.SavedCourseReport
import live.lb_trip.domain.model.TourPlaceVisit
import live.lb_trip.domain.repository.SavedCourseRepository
import live.lb_trip.domain.util.mapApiFailure
import live.lb_trip.domain.util.suspendRunCatching
import javax.inject.Inject
import kotlin.math.roundToInt

class SavedCourseRepositoryImpl @Inject constructor(
    private val savedCourseRemoteDataSource: SavedCourseRemoteDataSource,
    private val savedCourseDistanceLocalDataSource: SavedCourseDistanceLocalDataSource,
) : SavedCourseRepository {

    override suspend fun getSavedCourses(): Result<SavedCourseList> =
        suspendRunCatching {
            savedCourseRemoteDataSource.getSavedCourses().toDomain()
        }

    override suspend fun getSavedCourseDetail(savedCourseId: Long): Result<SavedCourseDetail> =
        suspendRunCatching {
            savedCourseRemoteDataSource.getSavedCourseDetail(savedCourseId).toDomain()
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
        }

    override suspend fun getSavedCourseReport(savedCourseId: Long): Result<SavedCourseReport> =
        suspendRunCatching {
            val report = savedCourseRemoteDataSource.getReport(savedCourseId).toDomain()
            report.copy(
                distanceWalkedMeters = report.distanceWalkedMeters
                    ?: savedCourseDistanceLocalDataSource.get(savedCourseId),
                stepCount = savedCourseDistanceLocalDataSource.getSteps(savedCourseId),
            )
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
            on(409, "TOUR_REPORT_NOT_AVAILABLE") throws LbTripSavedCourseException.TourReportNotAvailableException()
        }

    override suspend fun startTour(savedCourseId: Long): Result<List<TourPlaceVisit>> =
        suspendRunCatching {
            savedCourseRemoteDataSource.startTour(savedCourseId).toDomain()
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
            on(409, "TOUR_ALREADY_COMPLETED") throws LbTripSavedCourseException.TourAlreadyCompletedException()
        }

    override suspend fun checkInTourPlace(savedCourseId: Long, placeId: Long): Result<Unit> =
        suspendRunCatching {
            savedCourseRemoteDataSource.checkInTourPlace(savedCourseId, placeId)
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
            on(404, "SAVED_COURSE_PLACE_NOT_FOUND") throws LbTripSavedCourseException.SavedCoursePlaceNotFoundException()
            on(409, "TOUR_NOT_IN_PROGRESS") throws LbTripSavedCourseException.TourNotInProgressException()
        }

    override suspend fun endTour(savedCourseId: Long, distanceMeters: Float?): Result<Unit> =
        suspendRunCatching {
            savedCourseRemoteDataSource.endTour(savedCourseId, distanceMeters?.roundToInt())
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
            on(409, "TOUR_NOT_IN_PROGRESS") throws LbTripSavedCourseException.TourNotInProgressException()
        }

    override suspend fun saveTourMovement(savedCourseId: Long, movement: RecordedMovement) {
        savedCourseDistanceLocalDataSource.save(savedCourseId, movement.distanceMeters)
        savedCourseDistanceLocalDataSource.saveSteps(savedCourseId, movement.stepCount)
    }
}
