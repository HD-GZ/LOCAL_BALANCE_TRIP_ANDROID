package live.lb_trip.domain.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class SuspendRunCatchingTest {
    @Test
    fun `성공하면 Result success로 값을 감싼다`() =
        runTest {
            val result = suspendRunCatching { "ok" }

            assertEquals(Result.success("ok"), result)
        }

    @Test
    fun `일반 예외는 Result failure로 감싼다`() =
        runTest {
            val exception = IllegalStateException("boom")

            val result = suspendRunCatching { throw exception }

            assertTrue(result.isFailure)
            assertSame(exception, result.exceptionOrNull())
        }

    @Test(expected = CancellationException::class)
    fun `CancellationException은 Result로 감싸지 않고 다시 던진다`() =
        runTest {
            suspendRunCatching {
                throw CancellationException("cancelled")
            }
        }

    @Test(expected = TestError::class)
    fun `Exception이 아닌 Error는 Result로 감싸지 않고 다시 던진다`() =
        runTest {
            suspendRunCatching {
                throw TestError()
            }
            Unit
        }

    private class TestError : Error()
}
