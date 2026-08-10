package live.lb_trip.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class HomeHeroResponseDto(
    val items: List<HeroItemResponseDto>,
)

@Serializable
data class HeroItemResponseDto(
    val imageUrl: String,
    val title: String,
)

@Serializable
data class HomeProfileTypesResponseDto(
    val types: List<ProfileTypeResponseDto>,
)

@Serializable
data class ProfileTypeResponseDto(
    val code: String,
    val nickname: String,
    val description: String,
    val imageUrl: String,
)

@Serializable
data class HomeProfileSummaryResponseDto(
    val type: String,
    val description: String,
    val imageUrl: String,
    val diagnosedAt: String,
    val sliders: List<ProfileSliderResponseDto>,
)

@Serializable
data class ProfileSliderResponseDto(
    val key: String,
    val minLabel: String,
    val maxLabel: String,
    val score: Int,
)

@Serializable
data class HomeIncentivesResponseDto(
    val regions: List<RegionTabResponseDto>,
)

@Serializable
data class RegionTabResponseDto(
    val regionName: String,
    val ldongRegnCd: String,
    val ldongSignguCd: String,
    val incentives: List<IncentiveResponseDto>,
)

@Serializable
data class IncentiveResponseDto(
    val title: String,
    val description: String? = null,
    val url: String,
    val endDate: String? = null,
    val dday: Long? = null,
)

@Serializable
data class HomePopularCoursesResponseDto(
    val courses: List<PopularCourseResponseDto>,
)

@Serializable
data class PopularCourseResponseDto(
    val courseId: Long,
    val title: String,
    val reason: String,
    val imageUrl: String,
    val regionName: String,
)

@Serializable
data class HomeFeedResponseDto(
    val items: List<HomeFeedItemResponseDto>,
)

@Serializable
data class HomeFeedItemResponseDto(
    val itemType: String,
    val id: Long,
    val title: String,
    val imageUrl: String? = null,
    val subtitle: String? = null,
)

@Serializable
data class PopularCourseDetailResponseDto(
    val courseId: Long,
    val regionName: String,
    val title: String,
    val places: List<PlaceResponseDto>,
    val benefits: List<BenefitResponseDto> = emptyList(),
)
