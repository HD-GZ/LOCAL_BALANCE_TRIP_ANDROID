package live.lb_trip.feature.home

import live.lb_trip.domain.model.HomeFeedItem

sealed interface HomeIntent {
    data object Retry : HomeIntent
    data class IncentiveClicked(val card: HomeIncentiveCard) : HomeIntent
    data class FeedItemClicked(val item: HomeFeedItem) : HomeIntent
}
