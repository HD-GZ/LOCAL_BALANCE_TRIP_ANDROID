package live.lb_trip.domain.usecase

import javax.inject.Inject
import live.lb_trip.domain.repository.RecommendationRepository

class SaveCourseUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
) {
    suspend operator fun invoke(courseId: Long): Result<Unit> = recommendationRepository.saveCourse(courseId)
}
