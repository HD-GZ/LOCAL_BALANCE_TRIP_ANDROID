package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.ShareToken
import live.lb_trip.domain.repository.SavedCourseRepository
import javax.inject.Inject

class IssueShareTokenUseCase @Inject constructor(
    private val savedCourseRepository: SavedCourseRepository,
) {
    suspend operator fun invoke(savedCourseId: Long): Result<ShareToken> =
        savedCourseRepository.issueShareToken(savedCourseId)
}
