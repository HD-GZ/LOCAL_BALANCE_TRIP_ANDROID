package live.lb_trip.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetConfirmRequestDto(
    val email: String,
    val code: String,
)
