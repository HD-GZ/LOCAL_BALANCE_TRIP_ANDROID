package live.lb_trip.data.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import live.lb_trip.data.dto.request.EmailVerificationConfirmRequestDto
import live.lb_trip.data.dto.request.EmailVerificationResendRequestDto
import live.lb_trip.data.dto.request.LoginRequestDto
import live.lb_trip.data.dto.request.SignupRequestDto
import live.lb_trip.data.dto.response.TokenResponseDto
import live.lb_trip.data.dto.response.UserResponseDto
import live.lb_trip.data.dto.response.bodyOrThrow
import live.lb_trip.data.dto.response.checkOrThrow
import javax.inject.Inject
import javax.inject.Singleton
import live.lb_trip.data.di.qualifier.Auth
import live.lb_trip.data.di.qualifier.NoAuth

@Singleton
class AuthRemoteDataSource @Inject constructor(
    @NoAuth private val noAuthClient: HttpClient,
    @Auth private val authClient: HttpClient,
) {
    suspend fun signup(request: SignupRequestDto): UserResponseDto =
        noAuthClient.post("/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyOrThrow()

    suspend fun login(request: LoginRequestDto): TokenResponseDto =
        noAuthClient.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyOrThrow()

    suspend fun logout() =
        authClient.post("/auth/logout").checkOrThrow()

    suspend fun resendEmailVerification(request: EmailVerificationResendRequestDto): UserResponseDto =
        noAuthClient.post("/auth/email-verifications/resend") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyOrThrow()

    suspend fun confirmEmailVerification(request: EmailVerificationConfirmRequestDto): UserResponseDto =
        noAuthClient.post("/auth/email-verifications/confirm") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyOrThrow()
}
