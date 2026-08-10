package live.lb_trip.domain.repository

import live.lb_trip.domain.model.Receipt
import live.lb_trip.domain.model.ReceiptDetail
import live.lb_trip.domain.model.ReceiptScan
import live.lb_trip.domain.model.ReceiptSummary

interface ReceiptRepository {
    suspend fun getReceipts(savedCourseId: Long): Result<ReceiptSummary>

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

    suspend fun getReceiptDetail(
        savedCourseId: Long,
        receiptId: Long,
    ): Result<ReceiptDetail>

    suspend fun updateReceipt(
        savedCourseId: Long,
        receiptId: Long,
        merchantName: String,
        amount: Int,
        paidDate: String,
    ): Result<ReceiptDetail>

    suspend fun deleteReceipt(
        savedCourseId: Long,
        receiptId: Long,
    ): Result<Unit>

    suspend fun getReceiptDownloadUrl(
        savedCourseId: Long,
        receiptId: Long,
    ): Result<String>
}
