package live.lb_trip.data.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import live.lb_trip.data.di.qualifier.Auth
import live.lb_trip.data.di.qualifier.NoAuth
import live.lb_trip.data.dto.request.TourEndRequestDto
import live.lb_trip.data.dto.response.SavedCourseDetailResponseDto
import live.lb_trip.data.dto.response.SavedCourseListResponseDto
import live.lb_trip.data.dto.response.SavedCourseReportResponseDto
import live.lb_trip.data.dto.response.ShareTokenResponseDto
import live.lb_trip.data.dto.response.SharedCourseDetailResponseDto
import live.lb_trip.data.dto.response.TourStartResponseDto
import live.lb_trip.data.dto.response.bodyOrThrow
import live.lb_trip.data.dto.response.checkOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedCourseRemoteDataSource @Inject constructor(
    @Auth private val authClient: HttpClient,
    @NoAuth private val noAuthClient: HttpClient,
) {
    suspend fun getSavedCourses(): SavedCourseListResponseDto =
        authClient.get("/saved-courses") { parameter("limit", 50) }.bodyOrThrow()

    suspend fun getSavedCourseDetail(savedCourseId: Long): SavedCourseDetailResponseDto =
        authClient.get("/saved-courses/$savedCourseId").bodyOrThrow()

    suspend fun getReport(savedCourseId: Long): SavedCourseReportResponseDto =
        authClient.get("/saved-courses/$savedCourseId/report").bodyOrThrow()

    suspend fun startTour(savedCourseId: Long): TourStartResponseDto =
        authClient.post("/saved-courses/$savedCourseId/tour/start").bodyOrThrow()

    suspend fun checkInTourPlace(savedCourseId: Long, placeId: Long) {
        authClient.post("/saved-courses/$savedCourseId/tour/places/$placeId/check-in").checkOrThrow()
    }

    suspend fun endTour(savedCourseId: Long, walkedDistanceMeters: Int?) {
        authClient.post("/saved-courses/$savedCourseId/tour/end") {
            contentType(ContentType.Application.Json)
            setBody(TourEndRequestDto(walkedDistanceMeters))
        }.checkOrThrow()
    }

    suspend fun issueShareToken(savedCourseId: Long): ShareTokenResponseDto =
        authClient.post("/saved-courses/$savedCourseId/share-tokens").bodyOrThrow()

    suspend fun getSharedCourseDetail(token: String): SharedCourseDetailResponseDto =
        noAuthClient.get("/shared-courses/$token").bodyOrThrow()
}
