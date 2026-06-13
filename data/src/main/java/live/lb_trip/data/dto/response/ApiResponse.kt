package live.lb_trip.data.dto.response

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.Serializable
import live.lb_trip.domain.exception.ApiException

@Serializable
data class ApiResponse<T>(
    val result: String,
    val data: T? = null,
    val error: ApiError? = null,
)

@Serializable
data class ApiError(
    val code: String,
    val message: String,
    val data: List<ApiFieldError>? = null,
)

@Serializable
data class ApiFieldError(
    val field: String,
    val message: String,
)

suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T {
    val apiResponse = body<ApiResponse<T>>()
    if (apiResponse.result == "SUCCESS" && apiResponse.data != null) return apiResponse.data
    throw ApiException(
        statusCode = status.value,
        code = apiResponse.error?.code ?: "",
        message = apiResponse.error?.message ?: "",
    )
}

suspend fun HttpResponse.checkOrThrow() {
    val apiResponse = body<ApiResponse<Unit>>()
    if (apiResponse.result != "SUCCESS") {
        throw ApiException(
            statusCode = status.value,
            code = apiResponse.error?.code ?: "",
            message = apiResponse.error?.message ?: "",
        )
    }
}
