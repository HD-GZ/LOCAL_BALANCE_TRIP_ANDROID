package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.RegionIncentives
import live.lb_trip.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeIncentivesUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<RegionIncentives>> = homeRepository.getIncentives()
}
