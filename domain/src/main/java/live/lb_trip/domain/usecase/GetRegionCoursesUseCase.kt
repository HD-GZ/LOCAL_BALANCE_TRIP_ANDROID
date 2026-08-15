package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.RecommendedCourse
import live.lb_trip.domain.repository.RecommendationRepository
import javax.inject.Inject

class GetRegionCoursesUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
) {
    suspend operator fun invoke(regionId: Long): Result<List<RecommendedCourse>> =
        recommendationRepository.getRegionCourses(regionId)
}
