package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.CourseDetail
import live.lb_trip.domain.repository.HomeRepository
import javax.inject.Inject

class GetPopularCourseDetailUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(courseId: Long): Result<CourseDetail> = homeRepository.getPopularCourseDetail(courseId)
}
