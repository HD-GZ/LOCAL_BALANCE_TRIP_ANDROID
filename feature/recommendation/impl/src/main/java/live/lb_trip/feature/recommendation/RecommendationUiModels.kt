package live.lb_trip.feature.recommendation

data class CourseStop(
    val order: Int,
    val name: String,
    val hasAudioGuide: Boolean,
    val walkDuration: String?,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val audioUrl: String? = null,
)

data class RecommendationBenefit(
    val title: String,
    val description: String?,
    val url: String,
)
