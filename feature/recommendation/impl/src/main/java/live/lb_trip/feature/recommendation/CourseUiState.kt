package live.lb_trip.feature.recommendation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import live.lb_trip.domain.model.RecommendedCourse

data class CourseUiState(
    val isLoading: Boolean = true,
    val courses: ImmutableList<RecommendedCourse> = persistentListOf(),
)

sealed interface CourseSideEffect {
    data class ShowError(val reason: CourseLoadErrorReason) : CourseSideEffect
}

enum class CourseLoadErrorReason {
    RegionNotFound,
    Empty,
    Unknown,
}
