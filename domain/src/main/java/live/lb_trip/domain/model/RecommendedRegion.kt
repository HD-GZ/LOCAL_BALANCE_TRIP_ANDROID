package live.lb_trip.domain.model

data class RecommendedRegion(
    val id: Long,
    val name: String,
    val reason: String,
    val imageUrl: String?,
)
