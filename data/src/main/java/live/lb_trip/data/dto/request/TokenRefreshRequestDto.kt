package live.lb_trip.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class TokenRefreshRequestDto(
    val refreshToken: String,
)
