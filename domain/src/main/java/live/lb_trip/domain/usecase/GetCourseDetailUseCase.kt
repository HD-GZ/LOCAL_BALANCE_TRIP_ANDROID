package live.lb_trip.domain.usecase

import javax.inject.Inject
import live.lb_trip.domain.model.CourseDetail
import live.lb_trip.domain.repository.RecommendationRepository

class GetCourseDetailUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
) {
    suspend operator fun invoke(courseId: Long): Result<CourseDetail> =
        recommendationRepository.getCourseDetail(courseId)
}
