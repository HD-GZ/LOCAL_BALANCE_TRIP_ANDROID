package live.lb_trip.feature.savedcourses

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class SavedCoursesUiState(
    val isLoading: Boolean = true,
    val courses: ImmutableList<SavedCourseSummary> = persistentListOf(),
)

data class SavedCourseSummary(
    val courseId: Long,
    val regionName: String,
    val title: String,
    val reason: String,
)

sealed interface SavedCoursesSideEffect {
    data object ShowLoadError : SavedCoursesSideEffect
}
