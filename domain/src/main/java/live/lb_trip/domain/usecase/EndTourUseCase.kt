package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class EndTourUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(
        savedCourseId: Long,
        distanceMeters: Float?,
    ): Result<Unit> = savedCourseRepository.endTour(savedCourseId, distanceMeters)
}
