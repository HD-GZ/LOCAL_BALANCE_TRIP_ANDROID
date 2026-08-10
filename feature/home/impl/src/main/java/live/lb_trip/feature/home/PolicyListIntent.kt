package live.lb_trip.feature.home

sealed interface PolicyListIntent {
    data object Retry : PolicyListIntent
    data class CardClicked(val card: HomeIncentiveCard) : PolicyListIntent
}
