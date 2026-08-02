package live.lb_trip.domain.model

data class CourseDetail(
    val id: Long,
    val regionName: String,
    val title: String,
    val places: List<CoursePlace>,
)

data class CoursePlace(
    val order: Int,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val longitude: Double,
    val latitude: Double,
    val walkMinutes: Int?,
    val hasAudio: Boolean,
    val audioUrl: String?,
)
