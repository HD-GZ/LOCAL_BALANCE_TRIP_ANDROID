package live.lb_trip.data.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import live.lb_trip.data.di.qualifier.Auth
import live.lb_trip.data.di.qualifier.NoAuth
import live.lb_trip.data.dto.request.UserUpdateRequestDto
import live.lb_trip.data.dto.response.EmailAvailabilityResponseDto
import live.lb_trip.data.dto.response.UserProfileResponseDto
import live.lb_trip.data.dto.response.bodyOrThrow
import live.lb_trip.data.dto.response.checkOrThrow
import javax.inject.Inject
import javax.inject.Singleton

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

    suspend fun updateProfile(request: UserUpdateRequestDto): UserProfileResponseDto =
        authClient.patch("/users/me") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyOrThrow()

    suspend fun withdraw() =
        authClient.delete("/users/me").checkOrThrow()
}
