package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.ReceiptScan
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class ScanReceiptUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(
        savedCourseId: Long,
        imageBytes: ByteArray,
        fileName: String,
    ): Result<ReceiptScan> = savedCourseRepository.scanReceipt(savedCourseId, imageBytes, fileName)
}
