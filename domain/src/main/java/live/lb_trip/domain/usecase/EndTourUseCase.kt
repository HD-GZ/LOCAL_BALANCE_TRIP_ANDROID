package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class EndTourUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(savedCourseId: Long): Result<Unit> = savedCourseRepository.endTour(savedCourseId)
}
