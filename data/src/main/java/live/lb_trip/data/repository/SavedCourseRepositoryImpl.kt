package live.lb_trip.data.repository

import live.lb_trip.data.datasource.local.SavedCourseDistanceLocalDataSource
import live.lb_trip.data.datasource.remote.SavedCourseRemoteDataSource
import live.lb_trip.data.dto.request.TourReceiptCreateRequestDto
import live.lb_trip.data.mapper.toDomain
import live.lb_trip.domain.exception.savedcourse.LbTripSavedCourseException
import live.lb_trip.domain.model.Receipt
import live.lb_trip.domain.model.ReceiptScan
import live.lb_trip.domain.model.ReceiptSummary
import live.lb_trip.domain.model.RecordedMovement
import live.lb_trip.domain.model.SavedCourseDetail
import live.lb_trip.domain.model.SavedCourseList
import live.lb_trip.domain.model.SavedCourseReport
import live.lb_trip.domain.model.TourPlaceVisit
import live.lb_trip.domain.repository.SavedCourseRepository
import live.lb_trip.domain.util.mapApiFailure
import live.lb_trip.domain.util.suspendRunCatching
import javax.inject.Inject

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

    override suspend fun getReceipts(savedCourseId: Long): Result<ReceiptSummary> =
        suspendRunCatching {
            savedCourseRemoteDataSource.getReceipts(savedCourseId).toDomain()
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
        }

    override suspend fun getSavedCourseReport(savedCourseId: Long): Result<SavedCourseReport> =
        suspendRunCatching {
            val report = savedCourseRemoteDataSource.getReport(savedCourseId).toDomain()
            report.copy(
                distanceWalkedMeters = savedCourseDistanceLocalDataSource.get(savedCourseId),
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

    override suspend fun endTour(savedCourseId: Long): Result<Unit> =
        suspendRunCatching {
            savedCourseRemoteDataSource.endTour(savedCourseId)
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
            on(409, "TOUR_NOT_IN_PROGRESS") throws LbTripSavedCourseException.TourNotInProgressException()
        }

    override suspend fun saveTourMovement(savedCourseId: Long, movement: RecordedMovement) {
        savedCourseDistanceLocalDataSource.save(savedCourseId, movement.distanceMeters)
        savedCourseDistanceLocalDataSource.saveSteps(savedCourseId, movement.stepCount)
    }

    override suspend fun scanReceipt(
        savedCourseId: Long,
        imageBytes: ByteArray,
        fileName: String,
    ): Result<ReceiptScan> =
        suspendRunCatching {
            savedCourseRemoteDataSource.scanReceipt(savedCourseId, imageBytes, fileName).toDomain()
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
        }

    override suspend fun registerReceipt(
        savedCourseId: Long,
        imageId: Long,
        merchantName: String,
        amount: Int,
        paidDate: String,
    ): Result<Receipt> =
        suspendRunCatching {
            savedCourseRemoteDataSource.registerReceipt(
                savedCourseId,
                TourReceiptCreateRequestDto(
                    imageId = imageId,
                    merchantName = merchantName,
                    amount = amount,
                    paidDate = paidDate,
                ),
            ).toDomain()
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
            on(404, "IMAGE_NOT_FOUND") throws LbTripSavedCourseException.ImageNotFoundException()
            on(409, "IMAGE_ALREADY_ATTACHED") throws LbTripSavedCourseException.ImageAlreadyAttachedException()
        }
}
