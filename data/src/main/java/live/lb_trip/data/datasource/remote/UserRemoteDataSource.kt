package live.lb_trip.data.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import live.lb_trip.data.dto.response.EmailAvailabilityResponseDto
import live.lb_trip.data.dto.response.UserProfileResponseDto
import live.lb_trip.data.dto.response.bodyOrThrow
import javax.inject.Inject
import javax.inject.Singleton
import live.lb_trip.data.di.qualifier.Auth
import live.lb_trip.data.di.qualifier.NoAuth

@Singleton
class UserRemoteDataSource @Inject constructor(
    @NoAuth private val noAuthClient: HttpClient,
    @Auth private val authClient: HttpClient,
) {
    suspend fun checkEmailAvailability(email: String): Boolean =
        noAuthClient.get("/users/email-availability") {
            parameter("email", email)
        }.bodyOrThrow<EmailAvailabilityResponseDto>().available

    suspend fun getMyProfile(): UserProfileResponseDto =
        authClient.get("/users/me").bodyOrThrow()
}
