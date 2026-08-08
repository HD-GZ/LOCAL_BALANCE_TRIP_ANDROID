package live.lb_trip.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRequestDto(
    val resetToken: String,
    val newPassword: String,
)
