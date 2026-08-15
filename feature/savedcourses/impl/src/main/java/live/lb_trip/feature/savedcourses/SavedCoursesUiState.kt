package live.lb_trip.feature.savedcourses

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import live.lb_trip.domain.model.TravelStatus

data class SavedCoursesUiState(
    val isLoading: Boolean = true,
    val courses: ImmutableList<SavedCourseSummary> = persistentListOf(),
)

data class SavedCourseSummary(
    val savedCourseId: Long,
    val courseName: String,
    val status: TravelStatus,
    val imageUrl: String?,
)

sealed interface SavedCoursesSideEffect {
    data object ShowLoadError : SavedCoursesSideEffect
}
