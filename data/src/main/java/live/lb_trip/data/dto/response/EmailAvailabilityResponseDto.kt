package live.lb_trip.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class EmailAvailabilityResponseDto(
    val available: Boolean,
)
