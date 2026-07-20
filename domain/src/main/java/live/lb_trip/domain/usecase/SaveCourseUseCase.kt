package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.RecommendationRepository
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class SaveCourseUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(courseId: Long): Result<Unit> =
        recommendationRepository
            .saveCourse(courseId)
            .onSuccess { savedCourseRepository.markSaved(courseId) }
}
