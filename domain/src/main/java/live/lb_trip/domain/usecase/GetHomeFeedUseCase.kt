package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.HomeFeedItem
import live.lb_trip.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeFeedUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<HomeFeedItem>> = homeRepository.getHomeFeed()
}
