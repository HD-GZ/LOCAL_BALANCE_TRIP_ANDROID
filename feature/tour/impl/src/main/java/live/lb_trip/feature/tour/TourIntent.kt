package live.lb_trip.feature.tour

sealed interface TourIntent {
    data object NextStopArrived : TourIntent
    data class StopSelected(val index: Int) : TourIntent
    data object EndTourClicked : TourIntent
    data object Retry : TourIntent
    data object LocationTrackingStarted : TourIntent
    data object LocationTrackingStopped : TourIntent
    data object DistanceRecordingPermissionGranted : TourIntent
}
