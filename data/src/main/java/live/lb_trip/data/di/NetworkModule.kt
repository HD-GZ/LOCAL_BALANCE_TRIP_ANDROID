package live.lb_trip.data.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import live.lb_trip.data.di.qualifier.BaseUrl
import live.lb_trip.data.di.qualifier.Language
import live.lb_trip.data.di.qualifier.NoAuth
import live.lb_trip.data.dto.response.ApiResponse
import live.lb_trip.domain.exception.ApiException

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @NoAuth
    fun provideNoAuthHttpClient(
        @BaseUrl baseUrl: String,
        @Language language: String,
    ): HttpClient = HttpClient(OkHttp) {
        installCommon(baseUrl, language)
    }
}

internal const val REFRESH_TOKEN_PATH = "/auth/refresh"

private val validatorJson = Json { ignoreUnknownKeys = true }

private const val REQUEST_TIMEOUT_MILLIS = 60_000L

internal fun HttpClientConfig<OkHttpConfig>.installCommon(baseUrl: String, language: String) {
    defaultRequest {
        url(baseUrl)
        header(HttpHeaders.AcceptLanguage, language)
    }
    install(HttpTimeout) {
        requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
        connectTimeoutMillis = REQUEST_TIMEOUT_MILLIS
        socketTimeoutMillis = REQUEST_TIMEOUT_MILLIS
    }
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            },
        )
    }
    install(Logging) {
        level = LogLevel.ALL
        logger = object : Logger {
            override fun log(message: String) {
                Log.d("Ktor", message)
            }
        }
    }
    HttpResponseValidator {
        validateResponse { response ->
            if (response.status.isSuccess()) return@validateResponse
            if (response.request.url.encodedPath == REFRESH_TOKEN_PATH) return@validateResponse
            val body = response.bodyAsText()
            val apiError = runCatching {
                validatorJson.decodeFromString<ApiResponse<Unit>>(body).error
            }.getOrNull()
            throw ApiException(
                statusCode = response.status.value,
                code = apiError?.code ?: "",
                message = apiError?.message ?: response.status.description,
                fieldErrors = apiError?.data?.map { it.field to it.message } ?: emptyList(),
            )
        }
    }
}
