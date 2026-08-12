package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.SharedCourseDetail
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class GetSharedCourseDetailUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(token: String): Result<SharedCourseDetail> =
        savedCourseRepository.getSharedCourseDetail(token)
}
