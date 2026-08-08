package live.lb_trip.domain.repository

import live.lb_trip.domain.model.Gender
import live.lb_trip.domain.model.PasswordResetCodeIssued
import live.lb_trip.domain.model.PasswordResetToken
import live.lb_trip.domain.model.Tokens
import live.lb_trip.domain.model.User

interface AuthRepository {
    suspend fun signup(
        name: String,
        email: String,
        password: String,
        passwordConfirm: String,
        birthDate: String,
        gender: Gender,
        termsAgreed: Boolean,
        privacyAgreed: Boolean,
        marketingAgreed: Boolean,
    ): Result<User>

    suspend fun login(
        email: String,
        password: String,
    ): Result<Tokens>

    suspend fun logout(): Result<Unit>

    suspend fun resendEmailVerification(email: String): Result<User>

    suspend fun confirmEmailVerification(code: String): Result<User>

    suspend fun requestPasswordReset(email: String): Result<PasswordResetCodeIssued>

    suspend fun confirmPasswordReset(
        email: String,
        code: String,
    ): Result<PasswordResetToken>

    suspend fun resetPassword(
        resetToken: String,
        newPassword: String,
    ): Result<Unit>
}
