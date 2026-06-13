package live.lb_trip.domain.repository

import kotlinx.coroutines.flow.Flow
import live.lb_trip.domain.model.Tokens

interface UserRepository {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    fun getTokens(): Flow<Tokens?>
    suspend fun clearTokens()

    suspend fun checkEmailAvailability(email: String): Boolean
}
