package live.lb_trip.feature.propensity

import kotlinx.serialization.Serializable

@Serializable
data class PropensityRoute(val forceNew: Boolean = false)
