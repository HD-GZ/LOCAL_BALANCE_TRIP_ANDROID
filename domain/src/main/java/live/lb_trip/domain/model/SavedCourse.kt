package live.lb_trip.domain.model

enum class TravelStatus {
    BEFORE_TRIP,
    TRAVELING,
    COMPLETED,
}

data class SavedCourse(
    val savedCourseId: Long,
    val courseName: String,
    val imageUrl: String?,
    val status: TravelStatus,
)

data class SavedCourseList(
    val totalCount: Long,
    val courses: List<SavedCourse>,
)

data class SavedCourseDetail(
    val savedCourseId: Long,
    val regionName: String,
    val title: String,
    val status: TravelStatus,
    val places: List<CoursePlace>,
    val benefits: List<CourseBenefit>,
)

data class CourseBenefit(
    val title: String,
    val description: String?,
    val url: String,
)

data class SavedCourseReport(
    val courseName: String,
    val imageUrl: String?,
    val visitedPlaceCount: Int,
    val durationMinutes: Long,
    val totalSpentAmount: Int,
    val tourEndedAt: String,
)

data class TourPlaceVisit(
    val placeId: Long,
    val order: Int,
    val visited: Boolean,
)
