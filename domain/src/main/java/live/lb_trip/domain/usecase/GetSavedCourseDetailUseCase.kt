package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.SavedCourseDetail
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class GetSavedCourseDetailUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(savedCourseId: Long): Result<SavedCourseDetail> =
        savedCourseRepository.getSavedCourseDetail(savedCourseId)
}
