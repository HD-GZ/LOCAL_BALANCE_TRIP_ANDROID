package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.ReceiptScan
import live.lb_trip.domain.repository.ReceiptRepository
import javax.inject.Inject

class ScanReceiptUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
) {
    suspend operator fun invoke(
        savedCourseId: Long,
        imageBytes: ByteArray,
        fileName: String,
    ): Result<ReceiptScan> = receiptRepository.scanReceipt(savedCourseId, imageBytes, fileName)
}
