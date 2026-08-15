package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.SavedCourseList
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class GetSavedCoursesUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(): Result<SavedCourseList> = savedCourseRepository.getSavedCourses()
}
