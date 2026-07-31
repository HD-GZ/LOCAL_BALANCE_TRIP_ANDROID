package live.lb_trip.data.repository

import javax.inject.Inject
import live.lb_trip.data.datasource.remote.SavedCourseRemoteDataSource
import live.lb_trip.data.dto.request.TourReceiptCreateRequestDto
import live.lb_trip.data.mapper.toDomain
import live.lb_trip.domain.exception.savedcourse.LbTripSavedCourseException
import live.lb_trip.domain.model.Receipt
import live.lb_trip.domain.model.ReceiptScan
import live.lb_trip.domain.model.ReceiptSummary
import live.lb_trip.domain.model.SavedCourseDetail
import live.lb_trip.domain.model.SavedCourseList
import live.lb_trip.domain.repository.SavedCourseRepository
import live.lb_trip.domain.util.mapApiFailure
import live.lb_trip.domain.util.suspendRunCatching

class SavedCourseRepositoryImpl
    @Inject
    constructor(
        private val savedCourseRemoteDataSource: SavedCourseRemoteDataSource,
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
