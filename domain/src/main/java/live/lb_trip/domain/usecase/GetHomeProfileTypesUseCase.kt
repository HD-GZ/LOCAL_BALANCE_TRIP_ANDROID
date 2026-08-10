package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.ProfileType
import live.lb_trip.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeProfileTypesUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<ProfileType>> = homeRepository.getProfileTypes()
}
