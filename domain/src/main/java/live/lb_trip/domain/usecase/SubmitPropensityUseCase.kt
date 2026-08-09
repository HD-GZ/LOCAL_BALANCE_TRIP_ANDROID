package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.Preference
import live.lb_trip.domain.model.PropensityResult
import live.lb_trip.domain.model.ValueConsumption
import live.lb_trip.domain.repository.PropensityRepository
import javax.inject.Inject

class SubmitPropensityUseCase @Inject constructor(
    private val propensityRepository: PropensityRepository,
) {
    suspend operator fun invoke(
        preference: Preference,
        valueConsumption: ValueConsumption,
    ): Result<PropensityResult> =
        propensityRepository.submitPropensity(preference = preference, valueConsumption = valueConsumption)
}
