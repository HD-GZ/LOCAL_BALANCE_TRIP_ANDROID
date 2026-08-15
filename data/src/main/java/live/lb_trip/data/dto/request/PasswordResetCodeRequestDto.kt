package live.lb_trip.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetCodeRequestDto(
    val email: String,
)
