package live.lb_trip.feature.home

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import live.lb_trip.domain.model.CourseBenefit

data class PopularCourseDetailUiState(
    val isLoading: Boolean = true,
    val regionName: String = "",
    val title: String = "",
    val stops: ImmutableList<PopularCourseStop> = persistentListOf(),
    val expandedStopIndices: PersistentSet<Int> = persistentSetOf(0),
    val benefits: ImmutableList<CourseBenefit> = persistentListOf(),
    val walkTotalMinutes: Int = 0,
    val playingStopIndex: Int? = null,
) {
    val audioGuideCount: Int get() = stops.count { it.hasAudioGuide }
}

data class PopularCourseStop(
    val order: Int,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val latitude: Double,
    val longitude: Double,
    val walkToNextMinutes: Int?,
    val hasAudioGuide: Boolean,
    val audioUrl: String?,
)

sealed interface PopularCourseDetailSideEffect {
    data class ShowLoadError(val reason: PopularCourseDetailLoadErrorReason) : PopularCourseDetailSideEffect
    data class OpenBenefitUrl(val url: String) : PopularCourseDetailSideEffect
    data class OpenMap(val latitude: Double, val longitude: Double, val label: String) : PopularCourseDetailSideEffect
}

enum class PopularCourseDetailLoadErrorReason {
    CourseNotFound,
    EmptyPlaces,
    Unknown,
}
