package live.lb_trip.feature.home

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class HomeUiState(
    val savedCourses: PersistentList<SavedCourseUi> = persistentListOf(),
    val isSavedCoursesLoading: Boolean = false,
    val isSavedCoursesLoadError: Boolean = false,
)

data class SavedCourseUi(
    val courseId: Long,
    val regionName: String,
    val title: String,
    val stopCount: Int,
)
