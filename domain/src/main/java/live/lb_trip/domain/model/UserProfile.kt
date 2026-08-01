package live.lb_trip.domain.model

data class UserProfile(
    val name: String,
    val email: String,
    val birthDate: String,
    val gender: Gender,
)
