package live.lb_trip.domain.repository

import live.lb_trip.domain.model.CourseDetail
import live.lb_trip.domain.model.RecommendedCourse
import live.lb_trip.domain.model.RecommendedRegion

interface RecommendationRepository {
    suspend fun createRecommendations(): Result<Unit>

    suspend fun getRegions(): Result<List<RecommendedRegion>>

    suspend fun getRegionCourses(regionId: Long): Result<List<RecommendedCourse>>

    suspend fun getCourseDetail(courseId: Long): Result<CourseDetail>

    suspend fun saveCourse(courseId: Long): Result<Unit>
}
