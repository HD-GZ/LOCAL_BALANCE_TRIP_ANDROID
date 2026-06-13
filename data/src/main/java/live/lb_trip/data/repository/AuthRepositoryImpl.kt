package live.lb_trip.data.repository

import live.lb_trip.data.datasource.local.TokenDataStore
import live.lb_trip.data.datasource.remote.AuthRemoteDataSource
import live.lb_trip.data.dto.request.EmailVerificationConfirmRequestDto
import live.lb_trip.data.dto.request.EmailVerificationResendRequestDto
import live.lb_trip.data.dto.request.LoginRequestDto
import live.lb_trip.data.dto.request.SignupRequestDto
import live.lb_trip.data.mapper.toDomain
import live.lb_trip.domain.model.Gender
import live.lb_trip.domain.model.Tokens
import live.lb_trip.domain.model.User
import live.lb_trip.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {

    override suspend fun signup(
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
    ): User = authRemoteDataSource.signup(
        SignupRequestDto(
            name = name,
            email = email,
            password = password,
            passwordConfirm = passwordConfirm,
            phoneNumber = phoneNumber,
            birthDate = birthDate,
            gender = gender.name,
            termsAgreed = termsAgreed,
            privacyAgreed = privacyAgreed,
            marketingAgreed = marketingAgreed,
        ),
    ).toDomain()

    override suspend fun login(email: String, password: String): Tokens {
        val dto = authRemoteDataSource.login(LoginRequestDto(email = email, password = password))
        tokenDataStore.save(dto.accessToken, dto.refreshToken)
        return Tokens(accessToken = dto.accessToken, refreshToken = dto.refreshToken)
    }

    override suspend fun logout() {
        authRemoteDataSource.logout()
        tokenDataStore.clear()
    }

    override suspend fun resendEmailVerification(email: String): User =
        authRemoteDataSource.resendEmailVerification(
            EmailVerificationResendRequestDto(email = email),
        ).toDomain()

    override suspend fun confirmEmailVerification(code: String): User =
        authRemoteDataSource.confirmEmailVerification(
            EmailVerificationConfirmRequestDto(code = code),
        ).toDomain()
}
