package live.lb_trip.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateRequestDto(
    val name: String,
    val birthDate: String,
    val gender: String,
    val password: String? = null,
    val passwordConfirm: String? = null,
)
