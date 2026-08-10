package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.ReceiptDetail
import live.lb_trip.domain.repository.ReceiptRepository
import javax.inject.Inject

class UpdateReceiptUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
) {
    suspend operator fun invoke(
        savedCourseId: Long,
        receiptId: Long,
        merchantName: String,
        amount: Int,
        paidDate: String,
    ): Result<ReceiptDetail> = receiptRepository.updateReceipt(savedCourseId, receiptId, merchantName, amount, paidDate)
}
