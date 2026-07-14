package live.lb_trip.domain.usecase

import javax.inject.Inject
import live.lb_trip.domain.model.RecommendedCourse
import live.lb_trip.domain.repository.RecommendationRepository

class GetRegionCoursesUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
) {
    suspend operator fun invoke(regionId: Long): Result<List<RecommendedCourse>> =
        recommendationRepository.getRegionCourses(regionId)
}
