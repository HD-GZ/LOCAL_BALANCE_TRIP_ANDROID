package live.lb_trip.feature.home

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class HomeUiState(
    val isLoadingCourses: Boolean = true,
    val courses: ImmutableList<HomeCourseSummary> = persistentListOf(),
)

data class HomeCourseSummary(
    val courseId: Long,
    val regionName: String,
    val title: String,
    val reason: String,
)

sealed interface HomeSideEffect {
    data object ShowCoursesLoadError : HomeSideEffect
}
