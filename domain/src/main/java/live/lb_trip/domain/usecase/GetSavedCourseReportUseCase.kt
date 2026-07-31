package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.SavedCourseReport
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class GetSavedCourseReportUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(savedCourseId: Long): Result<SavedCourseReport> =
        savedCourseRepository.getSavedCourseReport(savedCourseId)
}
