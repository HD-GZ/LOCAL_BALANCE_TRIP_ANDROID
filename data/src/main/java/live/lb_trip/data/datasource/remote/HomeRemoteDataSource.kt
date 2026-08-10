package live.lb_trip.data.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton
import live.lb_trip.data.di.qualifier.Auth
import live.lb_trip.data.di.qualifier.NoAuth
import live.lb_trip.data.dto.response.HomeFeedResponseDto
import live.lb_trip.data.dto.response.HomeHeroResponseDto
import live.lb_trip.data.dto.response.HomeIncentivesResponseDto
import live.lb_trip.data.dto.response.HomePopularCoursesResponseDto
import live.lb_trip.data.dto.response.HomeProfileSummaryResponseDto
import live.lb_trip.data.dto.response.HomeProfileTypesResponseDto
import live.lb_trip.data.dto.response.bodyOrThrow

@Singleton
class HomeRemoteDataSource @Inject constructor(
    @NoAuth private val noAuthClient: HttpClient,
    @Auth private val authClient: HttpClient,
) {
    suspend fun getHero(): HomeHeroResponseDto = noAuthClient.get("/home/hero").bodyOrThrow()

    suspend fun getProfileTypes(): HomeProfileTypesResponseDto = noAuthClient.get("/home/profile-types").bodyOrThrow()

    suspend fun getIncentives(): HomeIncentivesResponseDto = noAuthClient.get("/home/incentives").bodyOrThrow()

    suspend fun getPopularCourses(): HomePopularCoursesResponseDto =
        noAuthClient.get("/home/popular-courses").bodyOrThrow()

    suspend fun getProfileSummary(): HomeProfileSummaryResponseDto = authClient.get("/home/profile-summary").bodyOrThrow()

    suspend fun getHomeFeed(): HomeFeedResponseDto = authClient.get("/home/saved-courses").bodyOrThrow()
}
