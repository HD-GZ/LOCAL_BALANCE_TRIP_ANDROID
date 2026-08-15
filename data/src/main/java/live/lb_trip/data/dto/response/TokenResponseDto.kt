package live.lb_trip.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponseDto(
    val accessToken: String,
    val refreshToken: String,
)
