package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.HeroItem
import live.lb_trip.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeHeroUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<HeroItem>> = homeRepository.getHero()
}
