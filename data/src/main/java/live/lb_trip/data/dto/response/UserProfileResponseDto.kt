package live.lb_trip.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponseDto(
    val name: String,
    val email: String,
    val birthDate: String? = null,
    val gender: String? = null,
)
