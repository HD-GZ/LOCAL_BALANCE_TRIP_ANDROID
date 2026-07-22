package live.lb_trip.feature.home

sealed interface HomeIntent {
    data object Retry : HomeIntent
}
