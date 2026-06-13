package live.lb_trip.data.dto.response

import kotlinx.serialization.Serializable
import live.lb_trip.data.exception.NetworkException

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

fun <T> ApiResponse<T>.getOrThrow(): T {
    if (result == "SUCCESS" && data != null) return data
    throw NetworkException(
        code = error?.code ?: "UNKNOWN_ERROR",
        message = error?.message ?: "Unknown error occurred",
    )
}

fun ApiResponse<Unit>.checkOrThrow() {
    if (result != "SUCCESS") {
        throw NetworkException(
            code = error?.code ?: "UNKNOWN_ERROR",
            message = error?.message ?: "Unknown error occurred",
        )
    }
}
