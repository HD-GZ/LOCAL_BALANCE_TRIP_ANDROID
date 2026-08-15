package live.lb_trip.domain.model

data class User(
    val userId: Long,
    val email: String,
    val status: UserStatus,
)
