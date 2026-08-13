package live.lb_trip.feature.savedcourses

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import live.lb_trip.domain.model.TravelStatus

data class SharedCourseDetailUiState(
    val isLoading: Boolean = true,
    val sharedByName: String = "",
    val imageUrl: String? = null,
    val regionName: String = "",
    val title: String = "",
    val status: TravelStatus = TravelStatus.BEFORE_TRIP,
    val stops: ImmutableList<SharedCourseStop> = persistentListOf(),
    val expandedStopIndices: PersistentSet<Int> = persistentSetOf(0),
    val playingStopIndex: Int? = null,
    val isAudioPlaying: Boolean = false,
    val audioPositionMs: Int = 0,
    val audioDurationMs: Int = 0,
    val walkTotalMinutes: Int = 0,
    val audioGuideCount: Int = 0,
    val benefits: ImmutableList<SavedCourseBenefit> = persistentListOf(),
    val loadFailed: Boolean = false,
)

data class SharedCourseStop(
    val order: Int,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val latitude: Double,
    val longitude: Double,
    val walkToNextMinutes: Int?,
    val hasAudioGuide: Boolean,
    val audioUrl: String? = null,
)

sealed interface SharedCourseDetailSideEffect {
    data class OpenBenefitUrl(val url: String) : SharedCourseDetailSideEffect
    data class OpenMap(val latitude: Double, val longitude: Double, val label: String) : SharedCourseDetailSideEffect
    data class ShowLoadError(val reason: SharedCourseDetailLoadErrorReason) : SharedCourseDetailSideEffect
}

enum class SharedCourseDetailLoadErrorReason {
    TokenNotFound,
    TokenExpired,
    Unknown,
}
