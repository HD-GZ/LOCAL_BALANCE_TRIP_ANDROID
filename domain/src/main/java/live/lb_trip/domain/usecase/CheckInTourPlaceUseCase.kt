package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class CheckInTourPlaceUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(
        savedCourseId: Long,
        placeId: Long,
    ): Result<Unit> = savedCourseRepository.checkInTourPlace(savedCourseId, placeId)
}
