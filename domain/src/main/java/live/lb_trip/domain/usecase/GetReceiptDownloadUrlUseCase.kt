package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.ReceiptRepository
import javax.inject.Inject

class GetReceiptDownloadUrlUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
) {
    suspend operator fun invoke(
        savedCourseId: Long,
        receiptId: Long,
    ): Result<String> = receiptRepository.getReceiptDownloadUrl(savedCourseId, receiptId)
}
