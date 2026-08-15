package live.lb_trip.data.repository

import javax.inject.Inject
import live.lb_trip.data.datasource.remote.PropensityRemoteDataSource
import live.lb_trip.data.dto.request.PreferenceRequestDto
import live.lb_trip.data.dto.request.PropensityRequestDto
import live.lb_trip.data.dto.request.ValueConsumptionRequestDto
import live.lb_trip.data.mapper.toDomain
import live.lb_trip.domain.exception.propensity.LbTripPropensityException
import live.lb_trip.domain.model.Preference
import live.lb_trip.domain.model.PropensityResult
import live.lb_trip.domain.model.ValueConsumption
import live.lb_trip.domain.repository.PropensityRepository
import live.lb_trip.domain.util.mapApiFailure
import live.lb_trip.domain.util.suspendRunCatching

class PropensityRepositoryImpl @Inject constructor(
    private val propensityRemoteDataSource: PropensityRemoteDataSource,
) : PropensityRepository {

    override suspend fun submitPropensity(
        preference: Preference,
        valueConsumption: ValueConsumption,
    ): Result<PropensityResult> =
        suspendRunCatching {
            propensityRemoteDataSource.submitPropensity(
                PropensityRequestDto(
                    preference = PreferenceRequestDto(
                        locality = preference.locality,
                        frugality = preference.frugality,
                        experientiality = preference.experientiality,
                        vitality = preference.vitality,
                        sociality = preference.sociality,
                    ),
                    valueConsumption = ValueConsumptionRequestDto(
                        accommodation = valueConsumption.accommodation,
                        food = valueConsumption.food,
                        experience = valueConsumption.experience,
                        transportation = valueConsumption.transportation,
                        cafeExhibition = valueConsumption.cafeExhibition,
                    ),
                ),
            ).toDomain()
        }.mapApiFailure {
            on(400, "INVALID_INPUT_VALUE") throws LbTripPropensityException.InvalidInputException()
            on(401) throws LbTripPropensityException.UnauthenticatedException()
        }

    override suspend fun getPropensity(): Result<PropensityResult> =
        suspendRunCatching {
            propensityRemoteDataSource.getPropensity().toDomain()
        }.mapApiFailure {
            on(404, "PROPENSITY_NOT_FOUND") throws LbTripPropensityException.PropensityNotFoundException()
        }
}
