package live.lb_trip.data.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Singleton
import live.lb_trip.data.di.qualifier.Auth
import live.lb_trip.data.dto.request.PropensityRequestDto
import live.lb_trip.data.dto.response.PropensityResponseDto
import live.lb_trip.data.dto.response.bodyOrThrow

@Singleton
class PropensityRemoteDataSource @Inject constructor(
    @Auth private val authClient: HttpClient,
) {
    suspend fun submitPropensity(request: PropensityRequestDto): PropensityResponseDto =
        authClient.post("/propensity") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyOrThrow()
}
