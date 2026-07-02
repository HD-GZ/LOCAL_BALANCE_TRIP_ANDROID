package live.lb_trip.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class PropensityRequestDto(
    val preference: PreferenceRequestDto,
    val valueConsumption: ValueConsumptionRequestDto,
)

@Serializable
data class PreferenceRequestDto(
    val locality: Int,
    val frugality: Int,
    val experientiality: Int,
    val vitality: Int,
    val sociality: Int,
)

@Serializable
data class ValueConsumptionRequestDto(
    val accommodation: Int,
    val food: Int,
    val experience: Int,
    val transportation: Int,
    val cafeExhibition: Int,
)
