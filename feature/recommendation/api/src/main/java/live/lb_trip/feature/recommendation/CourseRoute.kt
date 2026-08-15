package live.lb_trip.feature.recommendation

import kotlinx.serialization.Serializable

@Serializable
data class CourseRoute(val regionId: Long, val regionName: String)
