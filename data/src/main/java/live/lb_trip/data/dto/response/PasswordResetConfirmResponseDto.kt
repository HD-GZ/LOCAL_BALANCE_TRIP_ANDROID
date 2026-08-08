package live.lb_trip.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetConfirmResponseDto(
    val resetToken: String,
    val resetTokenExpiresIn: Long,
)
