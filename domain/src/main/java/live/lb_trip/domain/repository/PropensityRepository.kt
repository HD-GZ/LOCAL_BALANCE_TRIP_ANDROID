package live.lb_trip.domain.repository

import live.lb_trip.domain.model.Preference
import live.lb_trip.domain.model.PropensityResult
import live.lb_trip.domain.model.ValueConsumption

interface PropensityRepository {
    suspend fun submitPropensity(
        preference: Preference,
        valueConsumption: ValueConsumption,
    ): Result<PropensityResult>

    suspend fun getPropensity(): Result<PropensityResult>
}
