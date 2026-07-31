package live.lb_trip.domain.repository

import live.lb_trip.domain.model.Receipt
import live.lb_trip.domain.model.ReceiptScan
import live.lb_trip.domain.model.ReceiptSummary
import live.lb_trip.domain.model.SavedCourseDetail
import live.lb_trip.domain.model.SavedCourseList
import live.lb_trip.domain.model.SavedCourseReport
import live.lb_trip.domain.model.TourPlaceVisit

interface SavedCourseRepository {
    suspend fun getSavedCourses(): Result<SavedCourseList>

    suspend fun getSavedCourseDetail(savedCourseId: Long): Result<SavedCourseDetail>

    suspend fun getReceipts(savedCourseId: Long): Result<ReceiptSummary>

    suspend fun getSavedCourseReport(savedCourseId: Long): Result<SavedCourseReport>

    suspend fun startTour(savedCourseId: Long): Result<List<TourPlaceVisit>>

    suspend fun checkInTourPlace(
        savedCourseId: Long,
        placeId: Long,
    ): Result<Unit>

    suspend fun endTour(savedCourseId: Long): Result<Unit>

    suspend fun scanReceipt(
        savedCourseId: Long,
        imageBytes: ByteArray,
        fileName: String,
    ): Result<ReceiptScan>

    suspend fun registerReceipt(
        savedCourseId: Long,
        imageId: Long,
        merchantName: String,
        amount: Int,
        paidDate: String,
    ): Result<Receipt>
}
