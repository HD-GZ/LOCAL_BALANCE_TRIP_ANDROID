package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.Receipt
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class RegisterReceiptUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(
        savedCourseId: Long,
        imageId: Long,
        merchantName: String,
        amount: Int,
        paidDate: String,
    ): Result<Receipt> = savedCourseRepository.registerReceipt(savedCourseId, imageId, merchantName, amount, paidDate)
}
