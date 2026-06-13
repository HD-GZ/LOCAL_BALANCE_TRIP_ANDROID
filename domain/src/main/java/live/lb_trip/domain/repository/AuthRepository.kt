package live.lb_trip.domain.repository

import live.lb_trip.domain.model.Gender
import live.lb_trip.domain.model.Tokens
import live.lb_trip.domain.model.User

interface AuthRepository {
    suspend fun signup(
        name: String,
        email: String,
        password: String,
        passwordConfirm: String,
        phoneNumber: String,
        birthDate: String,
        gender: Gender,
        termsAgreed: Boolean,
        privacyAgreed: Boolean,
        marketingAgreed: Boolean,
    ): User

    suspend fun login(email: String, password: String): Tokens

    suspend fun logout()

    suspend fun resendEmailVerification(email: String): User

    suspend fun confirmEmailVerification(code: String): User
}
