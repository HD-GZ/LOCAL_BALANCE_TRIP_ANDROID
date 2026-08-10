package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.ReceiptSummary
import live.lb_trip.domain.repository.ReceiptRepository
import javax.inject.Inject

class GetReceiptsUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
) {
    suspend operator fun invoke(savedCourseId: Long): Result<ReceiptSummary> =
        receiptRepository.getReceipts(savedCourseId)
}
