package live.lb_trip.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val passwordConfirm: String,
    val birthDate: String,
    val gender: String,
    val termsAgreed: Boolean,
    val privacyAgreed: Boolean,
    val marketingAgreed: Boolean,
)
