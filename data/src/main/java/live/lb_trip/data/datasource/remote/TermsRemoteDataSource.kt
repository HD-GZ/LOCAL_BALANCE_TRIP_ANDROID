package live.lb_trip.data.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import live.lb_trip.data.di.qualifier.NoAuth
import live.lb_trip.data.dto.response.TermsResponseDto
import live.lb_trip.data.dto.response.bodyOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TermsRemoteDataSource @Inject constructor(
    @NoAuth private val noAuthClient: HttpClient,
) {
    suspend fun getTerms(type: String): TermsResponseDto =
        noAuthClient.get("/terms/$type").bodyOrThrow()
}
