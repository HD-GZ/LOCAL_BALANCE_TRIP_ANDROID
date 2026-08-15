package live.lb_trip.feature.recommendation

sealed interface RegionIntent {
    data object Retry : RegionIntent
}
