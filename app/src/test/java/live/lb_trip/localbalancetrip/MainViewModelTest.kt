package live.lb_trip.localbalancetrip

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import live.lb_trip.domain.model.Gender
import live.lb_trip.domain.model.Tokens
import live.lb_trip.domain.model.UserProfile
import live.lb_trip.domain.repository.UserRepository
import live.lb_trip.domain.usecase.GetTokensUseCase
import live.lb_trip.domain.usecase.GetUserProfileUseCase
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @Test
    fun logoutCancelsInFlightSessionValidation() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
        try {
            val tokens = MutableStateFlow<Tokens?>(Tokens("access", "refresh"))
            val profileStarted = CompletableDeferred<Unit>()
            val profileCancelled = CompletableDeferred<Unit>()
            val repository = FakeUserRepository(tokens, profileStarted, profileCancelled)
            val viewModel = MainViewModel(
                getTokensUseCase = GetTokensUseCase(repository),
                getUserProfileUseCase = GetUserProfileUseCase(repository),
            )
            advanceUntilIdle()
            assertTrue(profileStarted.isCompleted)
            tokens.value = null
            advanceUntilIdle()

            assertTrue(profileCancelled.isCompleted)
        } finally {
            kotlinx.coroutines.Dispatchers.resetMain()
        }
    }

    private class FakeUserRepository(
        private val tokens: Flow<Tokens?>,
        private val profileStarted: CompletableDeferred<Unit>,
        private val profileCancelled: CompletableDeferred<Unit>,
    ) : UserRepository {
        override fun getTokens(): Flow<Tokens?> = tokens

        override suspend fun getMyProfile(): Result<UserProfile> {
            profileStarted.complete(Unit)
            try {
                awaitCancellation()
            } finally {
                profileCancelled.complete(Unit)
            }
        }

        override suspend fun saveTokens(accessToken: String, refreshToken: String) = Unit

        override suspend fun clearTokens() = Unit

        override suspend fun checkEmailAvailability(email: String): Result<Boolean> = error("unused")

        override suspend fun updateProfile(
            name: String,
            birthDate: String,
            gender: Gender,
            password: String?,
            passwordConfirm: String?,
        ): Result<UserProfile> = error("unused")

        override suspend fun withdraw(): Result<Unit> = error("unused")
    }
}
