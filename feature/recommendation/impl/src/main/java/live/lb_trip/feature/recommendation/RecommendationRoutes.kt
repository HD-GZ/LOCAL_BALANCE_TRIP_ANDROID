package live.lb_trip.feature.recommendation

import kotlinx.serialization.Serializable

@Serializable
internal object RegionRoute

@Serializable
internal data class CourseRoute(val regionId: Long, val regionName: String)

@Serializable
internal data class DetailRoute(val courseId: Long)
