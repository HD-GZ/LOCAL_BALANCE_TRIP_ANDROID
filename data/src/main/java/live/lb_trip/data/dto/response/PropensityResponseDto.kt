package live.lb_trip.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PropensityResponseDto(
    val propensityResult: PropensityResultResponseDto,
    val preference: PreferenceResponseDto,
    val valueConsumption: ValueConsumptionResponseDto,
)

@Serializable
data class PropensityResultResponseDto(
    val type: String? = null,
    val description: String? = null,
)

@Serializable
data class PreferenceResponseDto(
    val locality: Int,
    val frugality: Int,
    val experientiality: Int,
    val vitality: Int,
    val sociality: Int,
)

@Serializable
data class ValueConsumptionResponseDto(
    val accommodation: Int,
    val food: Int,
    val experience: Int,
    val transportation: Int,
    val cafeExhibition: Int,
)
