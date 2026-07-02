package live.lb_trip.data.mapper

import live.lb_trip.data.dto.response.PropensityResponseDto
import live.lb_trip.domain.model.Preference
import live.lb_trip.domain.model.PropensityResult
import live.lb_trip.domain.model.ValueConsumption

fun PropensityResponseDto.toDomain(): PropensityResult =
    PropensityResult(
        type = propensityResult.type ?: "",
        description = propensityResult.description ?: "",
        preference = Preference(
            locality = preference.locality,
            frugality = preference.frugality,
            experientiality = preference.experientiality,
            vitality = preference.vitality,
            sociality = preference.sociality,
        ),
        valueConsumption = ValueConsumption(
            accommodation = valueConsumption.accommodation,
            food = valueConsumption.food,
            experience = valueConsumption.experience,
            transportation = valueConsumption.transportation,
            cafeExhibition = valueConsumption.cafeExhibition,
        ),
    )
