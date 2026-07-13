package live.lb_trip.data.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import javax.inject.Inject
import javax.inject.Singleton
import live.lb_trip.data.di.qualifier.Auth
import live.lb_trip.data.dto.response.CourseDetailResponseDto
import live.lb_trip.data.dto.response.CourseResponseDto
import live.lb_trip.data.dto.response.RegionResponseDto
import live.lb_trip.data.dto.response.bodyOrThrow
import live.lb_trip.data.dto.response.checkOrThrow

@Singleton
class RecommendationRemoteDataSource @Inject constructor(
    @Auth private val authClient: HttpClient,
) {
    suspend fun createRecommendations() =
        authClient.post("/recommendations").checkOrThrow()

    suspend fun getRegions(): List<RegionResponseDto> =
        authClient.get("/recommendations/regions").bodyOrThrow()

    suspend fun getRegionCourses(regionId: Long): List<CourseResponseDto> =
        authClient.get("/recommendations/regions/$regionId/courses").bodyOrThrow()

    suspend fun getCourseDetail(courseId: Long): CourseDetailResponseDto =
        authClient.get("/recommendations/courses/$courseId").bodyOrThrow()

    suspend fun saveCourse(courseId: Long) =
        authClient.post("/recommendations/courses/$courseId/save").checkOrThrow()
}
