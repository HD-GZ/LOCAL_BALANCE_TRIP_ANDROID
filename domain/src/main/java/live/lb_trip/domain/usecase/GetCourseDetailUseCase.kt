package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.CourseDetail
import live.lb_trip.domain.repository.RecommendationRepository
import javax.inject.Inject

class GetCourseDetailUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
) {
    suspend operator fun invoke(courseId: Long): Result<CourseDetail> =
        recommendationRepository.getCourseDetail(courseId)
}
