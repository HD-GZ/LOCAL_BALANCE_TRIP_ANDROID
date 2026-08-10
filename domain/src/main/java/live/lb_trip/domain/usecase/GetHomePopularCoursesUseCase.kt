package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.PopularCourse
import live.lb_trip.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomePopularCoursesUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<PopularCourse>> = homeRepository.getPopularCourses()
}
