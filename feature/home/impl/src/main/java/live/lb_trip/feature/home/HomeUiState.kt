package live.lb_trip.feature.home

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import live.lb_trip.domain.model.TravelStatus

data class HomeUiState(
    val isLoadingCourses: Boolean = true,
    val courses: ImmutableList<HomeCourseSummary> = persistentListOf(),
)

data class HomeCourseSummary(
    val savedCourseId: Long,
    val courseName: String,
    val status: TravelStatus,
)

sealed interface HomeSideEffect {
    data object ShowCoursesLoadError : HomeSideEffect
}
