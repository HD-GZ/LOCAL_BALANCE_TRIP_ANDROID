package live.lb_trip.data.repository

import live.lb_trip.data.datasource.remote.ReceiptRemoteDataSource
import live.lb_trip.data.dto.request.TourReceiptCreateRequestDto
import live.lb_trip.data.dto.request.TourReceiptUpdateRequestDto
import live.lb_trip.data.mapper.toDomain
import live.lb_trip.domain.exception.savedcourse.LbTripSavedCourseException
import live.lb_trip.domain.model.Receipt
import live.lb_trip.domain.model.ReceiptDetail
import live.lb_trip.domain.model.ReceiptScan
import live.lb_trip.domain.model.ReceiptSummary
import live.lb_trip.domain.repository.ReceiptRepository
import live.lb_trip.domain.util.mapApiFailure
import live.lb_trip.domain.util.suspendRunCatching
import javax.inject.Inject

class ReceiptRepositoryImpl @Inject constructor(
    private val receiptRemoteDataSource: ReceiptRemoteDataSource,
) : ReceiptRepository {

    override suspend fun getReceipts(savedCourseId: Long): Result<ReceiptSummary> =
        suspendRunCatching {
            receiptRemoteDataSource.getReceipts(savedCourseId).toDomain()
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
        }

    override suspend fun scanReceipt(
        savedCourseId: Long,
        imageBytes: ByteArray,
        fileName: String,
    ): Result<ReceiptScan> =
        suspendRunCatching {
            receiptRemoteDataSource.scanReceipt(savedCourseId, imageBytes, fileName).toDomain()
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
            receiptRemoteDataSource.registerReceipt(
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

    override suspend fun getReceiptDetail(savedCourseId: Long, receiptId: Long): Result<ReceiptDetail> =
        suspendRunCatching {
            receiptRemoteDataSource.getReceiptDetail(savedCourseId, receiptId).toDomain()
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
            on(404, "TOUR_RECEIPT_NOT_FOUND") throws LbTripSavedCourseException.TourReceiptNotFoundException()
        }

    override suspend fun updateReceipt(
        savedCourseId: Long,
        receiptId: Long,
        merchantName: String,
        amount: Int,
        paidDate: String,
    ): Result<ReceiptDetail> =
        suspendRunCatching {
            receiptRemoteDataSource.updateReceipt(
                savedCourseId,
                receiptId,
                TourReceiptUpdateRequestDto(merchantName = merchantName, amount = amount, paidDate = paidDate),
            ).toDomain()
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
            on(404, "TOUR_RECEIPT_NOT_FOUND") throws LbTripSavedCourseException.TourReceiptNotFoundException()
        }

    override suspend fun deleteReceipt(savedCourseId: Long, receiptId: Long): Result<Unit> =
        suspendRunCatching {
            receiptRemoteDataSource.deleteReceipt(savedCourseId, receiptId)
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
            on(404, "TOUR_RECEIPT_NOT_FOUND") throws LbTripSavedCourseException.TourReceiptNotFoundException()
        }

    override suspend fun getReceiptDownloadUrl(savedCourseId: Long, receiptId: Long): Result<String> =
        suspendRunCatching {
            receiptRemoteDataSource.getReceiptDownloadUrl(savedCourseId, receiptId).downloadUrl
        }.mapApiFailure {
            on(404, "SAVED_COURSE_NOT_FOUND") throws LbTripSavedCourseException.SavedCourseNotFoundException()
            on(404, "TOUR_RECEIPT_NOT_FOUND") throws LbTripSavedCourseException.TourReceiptNotFoundException()
        }
}
