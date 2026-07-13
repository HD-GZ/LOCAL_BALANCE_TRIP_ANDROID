package live.lb_trip.domain.usecase

import javax.inject.Inject
import live.lb_trip.domain.repository.RecommendationRepository

class CreateRecommendationsUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
) {
    suspend operator fun invoke(): Result<Unit> = recommendationRepository.createRecommendations()
}
