package live.lb_trip.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class RegionResponseDto(
    val regionId: Long,
    val regionName: String,
    val imageUrl: String? = null,
    val reason: String,
)

@Serializable
data class CourseResponseDto(
    val courseId: Long,
    val title: String,
    val reason: String,
    val imageUrl: String? = null,
)

@Serializable
data class CourseDetailResponseDto(
    val courseId: Long,
    val regionName: String,
    val title: String,
    val places: List<PlaceResponseDto>,
    val benefits: List<BenefitResponseDto> = emptyList(),
)

@Serializable
data class PlaceResponseDto(
    val order: Int,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val longitude: Double,
    val latitude: Double,
    val walkMinutes: Int? = null,
    val hasAudio: Boolean,
    val audioUrl: String? = null,
)
