package live.lb_trip.feature.savedcourses

import kotlinx.serialization.Serializable

@Serializable
data class SavedCoursesRoute(val initialSavedCourseId: Long? = null)
