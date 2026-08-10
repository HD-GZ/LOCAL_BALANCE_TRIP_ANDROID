package live.lb_trip.domain.model

data class HeroItem(
    val imageUrl: String,
    val title: String,
)

data class ProfileType(
    val code: String,
    val nickname: String,
    val description: String,
    val imageUrl: String,
)

data class ProfileSummary(
    val type: String,
    val description: String,
    val imageUrl: String,
    val diagnosedAt: String,
    val sliders: List<ProfileSlider>,
)

data class ProfileSlider(
    val key: String,
    val minLabel: String,
    val maxLabel: String,
    val score: Int,
)

data class RegionIncentives(
    val regionName: String,
    val ldongRegnCd: String,
    val ldongSignguCd: String,
    val incentives: List<Incentive>,
)

data class Incentive(
    val title: String,
    val description: String?,
    val url: String,
    val endDate: String?,
    val dday: Long?,
)

data class PopularCourse(
    val courseId: Long,
    val title: String,
    val reason: String,
    val imageUrl: String,
    val regionName: String,
)

sealed interface HomeFeedItem {
    val id: Long
    val title: String
    val imageUrl: String?
    val subtitle: String?

    data class SavedCourseItem(
        override val id: Long,
        override val title: String,
        override val imageUrl: String?,
        override val subtitle: String?,
    ) : HomeFeedItem

    data class RecommendedRegionItem(
        override val id: Long,
        override val title: String,
        override val imageUrl: String?,
        override val subtitle: String?,
    ) : HomeFeedItem
}
