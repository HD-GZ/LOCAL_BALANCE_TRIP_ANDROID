package live.lb_trip.core.designsystem.component

data class LbTimelineStop(
    val order: Int,
    val name: String,
    val description: String?,
    val walkDuration: String?,
    val hasAudioGuide: Boolean,
)
