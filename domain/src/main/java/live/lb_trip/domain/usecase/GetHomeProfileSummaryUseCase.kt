package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.ProfileSummary
import live.lb_trip.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeProfileSummaryUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(): Result<ProfileSummary> = homeRepository.getProfileSummary()
}
