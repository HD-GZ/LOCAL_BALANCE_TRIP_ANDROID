package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.RecommendedRegion
import live.lb_trip.domain.repository.RecommendationRepository
import javax.inject.Inject

class GetRecommendedRegionsUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
) {
    suspend operator fun invoke(): Result<List<RecommendedRegion>> = recommendationRepository.getRegions()
}
