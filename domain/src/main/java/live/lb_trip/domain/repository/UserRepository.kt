package live.lb_trip.domain.repository

import kotlinx.coroutines.flow.Flow
import live.lb_trip.domain.model.Gender
import live.lb_trip.domain.model.Tokens
import live.lb_trip.domain.model.UserProfile

interface UserRepository {
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    )

    fun getTokens(): Flow<Tokens?>

    suspend fun clearTokens()

    suspend fun checkEmailAvailability(email: String): Result<Boolean>

    suspend fun getMyProfile(): Result<UserProfile>

    suspend fun updateProfile(
        name: String,
        birthDate: String,
        gender: Gender,
        password: String?,
        passwordConfirm: String?,
    ): Result<UserProfile>

    suspend fun withdraw(): Result<Unit>
}
