package live.lb_trip.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Singleton
import kotlinx.coroutines.flow.firstOrNull
import live.lb_trip.data.datasource.local.TokenDataStore
import live.lb_trip.data.di.qualifier.BaseUrl
import live.lb_trip.data.di.qualifier.Language
import live.lb_trip.data.di.qualifier.NoAuth
import live.lb_trip.data.dto.request.TokenRefreshRequestDto
import live.lb_trip.data.dto.response.ApiResponse
import live.lb_trip.data.dto.response.TokenResponseDto
import live.lb_trip.data.di.qualifier.Auth as AuthQualifier

@Module
@InstallIn(SingletonComponent::class)
object AuthTokenModule {

    @Provides
    @Singleton
    @AuthQualifier
    fun provideAuthHttpClient(
        @BaseUrl baseUrl: String,
        @Language language: String,
        @NoAuth noAuthClient: HttpClient,
        tokenDataStore: TokenDataStore,
    ): HttpClient = HttpClient(OkHttp) {
        installCommon(baseUrl, language)
        install(Auth) {
            bearer {
                // DataStore is the single source of truth for tokens; without this, Ktor caches
                // the BearerTokens it last loaded in memory and keeps reusing them even after
                // login/logout writes a different value to the DataStore, which can send a dead
                // token from a previous session and wipe out a freshly saved one on refresh failure.
                cacheTokens = false
                loadTokens {
                    tokenDataStore.tokens.firstOrNull()?.let {
                        BearerTokens(
                            accessToken = it.accessToken,
                            refreshToken = it.refreshToken,
                        )
                    }
                }
                refreshTokens {
                    val refreshToken = oldTokens?.refreshToken ?: return@refreshTokens null
                    val response = noAuthClient.post(REFRESH_TOKEN_PATH) {
                        markAsRefreshTokenRequest()
                        contentType(ContentType.Application.Json)
                        setBody(TokenRefreshRequestDto(refreshToken = refreshToken))
                    }
                    if (response.status.isSuccess()) {
                        val dto = response.body<ApiResponse<TokenResponseDto>>().data
                            ?: return@refreshTokens null
                        tokenDataStore.save(dto.accessToken, dto.refreshToken)
                        BearerTokens(
                            accessToken = dto.accessToken,
                            refreshToken = dto.refreshToken,
                        )
                    } else {
                        if (response.status == HttpStatusCode.Unauthorized) {
                            val currentRefreshToken = tokenDataStore.tokens.firstOrNull()?.refreshToken
                            if (currentRefreshToken == refreshToken) {
                                tokenDataStore.clear()
                            }
                        }
                        null
                    }
                }
            }
        }
    }
}
