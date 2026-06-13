package live.lb_trip.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import live.lb_trip.data.datasource.local.TokenDataStore
import live.lb_trip.data.di.qualifier.BaseUrl
import live.lb_trip.data.di.qualifier.NoAuth
import live.lb_trip.data.dto.request.TokenRefreshRequestDto
import live.lb_trip.data.dto.response.ApiResponse
import live.lb_trip.data.dto.response.TokenResponseDto
import live.lb_trip.domain.exception.ApiException
import live.lb_trip.data.di.qualifier.Auth as AuthQualifier

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("tokens") },
        )

    @Provides
    @Singleton
    @NoAuth
    fun provideNoAuthHttpClient(
        @BaseUrl baseUrl: String,
    ): HttpClient = HttpClient(OkHttp) {
        installCommon(baseUrl)
    }

    @Provides
    @Singleton
    @AuthQualifier
    fun provideAuthHttpClient(
        @BaseUrl baseUrl: String,
        @NoAuth noAuthClient: HttpClient,
        tokenDataStore: TokenDataStore,
    ): HttpClient = HttpClient(OkHttp) {
        installCommon(baseUrl)
        install(Auth) {
            bearer {
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
                    val response = noAuthClient.post("/auth/refresh") {
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
                        tokenDataStore.clear()
                        null
                    }
                }
            }
        }
    }
}

private val validatorJson = Json { ignoreUnknownKeys = true }

private fun HttpClientConfig<OkHttpConfig>.installCommon(baseUrl: String) {
    defaultRequest { url(baseUrl) }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        })
    }
    install(Logging) { level = LogLevel.INFO }
    HttpResponseValidator {
        validateResponse { response ->
            if (response.status.isSuccess()) return@validateResponse
            val body = response.bodyAsText()
            val apiError = runCatching {
                validatorJson.decodeFromString<ApiResponse<Unit>>(body).error
            }.getOrNull()
            throw ApiException(
                statusCode = response.status.value,
                code = apiError?.code ?: "",
                message = apiError?.message ?: response.status.description,
            )
        }
    }
}
