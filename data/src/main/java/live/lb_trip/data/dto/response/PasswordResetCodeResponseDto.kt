package live.lb_trip.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetCodeResponseDto(
    val verificationCodeExpiresIn: Long,
)
