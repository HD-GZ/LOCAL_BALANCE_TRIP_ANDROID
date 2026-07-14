package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.RecommendationRepository
import javax.inject.Inject

class CreateRecommendationsUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
) {
    suspend operator fun invoke(): Result<Unit> = recommendationRepository.createRecommendations()
}
