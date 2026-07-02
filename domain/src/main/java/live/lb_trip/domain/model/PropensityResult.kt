package live.lb_trip.domain.model

data class PropensityResult(
    val type: String,
    val description: String,
    val preference: Preference,
    val valueConsumption: ValueConsumption,
)
