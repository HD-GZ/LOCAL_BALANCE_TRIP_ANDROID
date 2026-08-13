package live.lb_trip.feature.tour

sealed interface TourIntent {
    data object NextStopArrived : TourIntent
    data class StopSelected(val index: Int) : TourIntent
    data object PlaybackToggled : TourIntent
    data class BenefitClicked(val url: String) : TourIntent
    data object FinishAcknowledged : TourIntent
    data object Retry : TourIntent
    data object LocationTrackingStarted : TourIntent
    data object LocationTrackingStopped : TourIntent
    data object DistanceRecordingPermissionGranted : TourIntent
    data object AudioPlaybackStopRequested : TourIntent
}
