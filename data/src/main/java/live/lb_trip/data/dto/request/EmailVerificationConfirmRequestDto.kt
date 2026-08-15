package live.lb_trip.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class EmailVerificationConfirmRequestDto(
    val code: String,
)
