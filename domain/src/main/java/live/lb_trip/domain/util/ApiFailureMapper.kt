package live.lb_trip.domain.util

import live.lb_trip.domain.exception.ApiException
import live.lb_trip.domain.exception.LbTripException

fun <T> Result<T>.mapApiFailure(block: ApiExceptionMapper.() -> Unit): Result<T> {
    val exception = exceptionOrNull() as? ApiException ?: return this
    val mapper = ApiExceptionMapper(exception)
    mapper.block()
    return Result.failure(mapper.map())
}

class ApiExceptionMapper(
    private val exception: ApiException,
) {
    private var mapped: LbTripException? = null

    inner class OnBuilder(
        private val matched: Boolean,
    ) {
        infix fun throws(e: LbTripException) {
            if (matched) mapped = e
        }
    }

    fun on(status: Int) = OnBuilder(exception.statusCode == status)

    fun on(statusRange: IntRange) = OnBuilder(exception.statusCode in statusRange)

    fun on(
        status: Int,
        code: String,
    ) = OnBuilder(exception.statusCode == status && exception.code == code)

    fun on(
        statusRange: IntRange,
        code: String,
    ) = OnBuilder(
        exception.statusCode in statusRange && exception.code == code,
    )

    fun on(
        status: Int,
        code: String,
        transform: (ApiException) -> LbTripException,
    ) {
        if (exception.statusCode == status && exception.code == code) mapped = transform(exception)
    }

    internal fun map(): LbTripException = mapped ?: exception
}
