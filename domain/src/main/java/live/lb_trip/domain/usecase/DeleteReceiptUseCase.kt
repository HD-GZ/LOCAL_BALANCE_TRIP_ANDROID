package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.ReceiptRepository
import javax.inject.Inject

class DeleteReceiptUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
) {
    suspend operator fun invoke(
        savedCourseId: Long,
        receiptId: Long,
    ): Result<Unit> = receiptRepository.deleteReceipt(savedCourseId, receiptId)
}
