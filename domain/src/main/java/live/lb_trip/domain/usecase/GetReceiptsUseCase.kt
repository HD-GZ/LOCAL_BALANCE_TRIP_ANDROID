package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.ReceiptSummary
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class GetReceiptsUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(savedCourseId: Long): Result<ReceiptSummary> =
        savedCourseRepository.getReceipts(savedCourseId)
}
