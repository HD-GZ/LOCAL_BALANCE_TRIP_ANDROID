package live.lb_trip.domain.model

data class LocationFix(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float?,
    val ageMillis: Long,
)
