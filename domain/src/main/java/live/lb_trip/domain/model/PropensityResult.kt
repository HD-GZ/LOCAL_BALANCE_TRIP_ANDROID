package live.lb_trip.domain.model

data class PropensityResult(
    val type: String,
    val code: String,
    val description: String,
    val imageUrl: String?,
    val preference: Preference,
    val valueConsumption: ValueConsumption,
)
