package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.ReceiptDetail
import live.lb_trip.domain.repository.ReceiptRepository
import javax.inject.Inject

class GetReceiptDetailUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
) {
    suspend operator fun invoke(
        savedCourseId: Long,
        receiptId: Long,
    ): Result<ReceiptDetail> = receiptRepository.getReceiptDetail(savedCourseId, receiptId)
}
