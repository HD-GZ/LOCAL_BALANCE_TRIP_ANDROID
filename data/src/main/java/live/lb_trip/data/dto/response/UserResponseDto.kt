package live.lb_trip.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDto(
    val userId: Long,
    val email: String,
    val status: String,
)
