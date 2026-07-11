package live.lb_trip.feature.recommendation

import kotlinx.serialization.Serializable

@Serializable
internal object RegionRoute

@Serializable
internal data class CourseRoute(val regionIndex: Int)

@Serializable
internal data class DetailRoute(val courseIndex: Int)
