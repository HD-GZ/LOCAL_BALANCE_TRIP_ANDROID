package live.lb_trip.data.repository

import kotlinx.coroutines.flow.Flow
import live.lb_trip.data.datasource.local.TokenDataStore
import live.lb_trip.data.datasource.remote.UserRemoteDataSource
import live.lb_trip.domain.exception.user.LbTripUserException
import live.lb_trip.domain.model.Tokens
import live.lb_trip.domain.repository.UserRepository
import live.lb_trip.domain.util.mapApiFailure
import live.lb_trip.domain.util.suspendRunCatching
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val userRemoteDataSource: UserRemoteDataSource,
) : UserRepository {

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenDataStore.save(accessToken, refreshToken)
    }

    override fun getTokens(): Flow<Tokens?> = tokenDataStore.tokens

    override suspend fun clearTokens() {
        tokenDataStore.clear()
    }

    override suspend fun checkEmailAvailability(email: String): Result<Boolean> =
        suspendRunCatching {
            userRemoteDataSource.checkEmailAvailability(email)
        }.mapApiFailure {
            on(409, "EMAIL_UNAVAILABLE") throws LbTripUserException.EmailUnavailableException()
        }
}
