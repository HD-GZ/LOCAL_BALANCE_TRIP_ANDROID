package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.Receipt
import live.lb_trip.domain.repository.ReceiptRepository
import javax.inject.Inject

class RegisterReceiptUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
) {
    suspend operator fun invoke(
        savedCourseId: Long,
        imageId: Long,
        merchantName: String,
        amount: Int,
        paidDate: String,
    ): Result<Receipt> = receiptRepository.registerReceipt(savedCourseId, imageId, merchantName, amount, paidDate)
}
