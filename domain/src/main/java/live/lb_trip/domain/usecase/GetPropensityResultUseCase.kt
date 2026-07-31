package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.PropensityResult
import live.lb_trip.domain.repository.PropensityRepository
import javax.inject.Inject

class GetPropensityResultUseCase
    @Inject
    constructor(
        private val propensityRepository: PropensityRepository,
    ) {
        suspend operator fun invoke(): Result<PropensityResult> = propensityRepository.getPropensity()
    }
