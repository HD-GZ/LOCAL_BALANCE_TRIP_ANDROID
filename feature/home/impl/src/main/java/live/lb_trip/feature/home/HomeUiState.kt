package live.lb_trip.feature.home

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import live.lb_trip.domain.model.HeroItem
import live.lb_trip.domain.model.HomeFeedItem
import live.lb_trip.domain.model.PopularCourse
import live.lb_trip.domain.model.ProfileSummary
import live.lb_trip.domain.model.ProfileType
import live.lb_trip.domain.model.RegionIncentives

data class HomeUiState(
    val isLoggedIn: Boolean = false,

    val isHeroLoading: Boolean = true,
    val heroItems: ImmutableList<HeroItem> = persistentListOf(),

    val isTypeSectionLoading: Boolean = true,
    val isDiagnosed: Boolean = false,
    val profileTypes: ImmutableList<ProfileType> = persistentListOf(),
    val profileSummary: ProfileSummary? = null,

    val isIncentivesLoading: Boolean = true,
    val incentiveCards: ImmutableList<HomeIncentiveCard> = persistentListOf(),

    val isFeedLoading: Boolean = true,
    val feed: ImmutableList<HomeFeedItem> = persistentListOf(),
    val popularCourses: ImmutableList<PopularCourse> = persistentListOf(),
)

data class HomeIncentiveCard(
    val regionName: String,
    val title: String,
    val description: String?,
    val url: String,
    val endDate: String?,
    val dday: Long?,
)

fun List<RegionIncentives>.toIncentiveCards(): ImmutableList<HomeIncentiveCard> = flatMap { region ->
    region.incentives.map { incentive ->
        HomeIncentiveCard(
            regionName = region.regionName,
            title = incentive.title,
            description = incentive.description,
            url = incentive.url,
            endDate = incentive.endDate,
            dday = incentive.dday,
        )
    }
}.sortedWith(compareBy(nullsLast()) { it.dday }).toPersistentList()

sealed interface HomeSideEffect {
    data object ShowFeedLoadError : HomeSideEffect
    data class OpenUrl(val url: String) : HomeSideEffect
    data class NavigateToRecommendedRegion(val regionId: Long, val regionName: String) : HomeSideEffect
}
