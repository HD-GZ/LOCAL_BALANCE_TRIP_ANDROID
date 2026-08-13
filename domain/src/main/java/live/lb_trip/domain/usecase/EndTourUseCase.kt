package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.TourEndResult
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class EndTourUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(
        savedCourseId: Long,
        distanceMeters: Float?,
    ): Result<TourEndResult> = savedCourseRepository.endTour(savedCourseId, distanceMeters)
}
