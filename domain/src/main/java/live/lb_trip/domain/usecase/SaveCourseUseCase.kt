package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.RecommendationRepository
import javax.inject.Inject

class SaveCourseUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
) {
    suspend operator fun invoke(courseId: Long): Result<Unit> = recommendationRepository.saveCourse(courseId)
}
