package live.lb_trip.domain.usecase

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import live.lb_trip.domain.model.CourseDetail
import live.lb_trip.domain.repository.RecommendationRepository
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class GetSavedCoursesUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
    private val recommendationRepository: RecommendationRepository,
) {
    suspend operator fun invoke(): Result<List<CourseDetail>> =
        runCatching {
            val courseIds = savedCourseRepository.observeSavedCourseIds().first()
            coroutineScope {
                courseIds
                    .map { courseId -> async { recommendationRepository.getCourseDetail(courseId).getOrNull() } }
                    .awaitAll()
                    .filterNotNull()
            }
        }
}
