package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.TourPlaceVisit
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class StartTourUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(savedCourseId: Long): Result<List<TourPlaceVisit>> =
        savedCourseRepository.startTour(savedCourseId)
}
