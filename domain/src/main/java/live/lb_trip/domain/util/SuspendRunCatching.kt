package live.lb_trip.domain.util

import kotlinx.coroutines.CancellationException

/**
 * 코루틴 취소(CancellationException)는 다시 던지고, JVM [Error]는 그대로 전파한다.
 * 그 외 [Exception]만 [Result.failure]로 감싼다.
 */
@Suppress("TooGenericExceptionCaught")
suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
