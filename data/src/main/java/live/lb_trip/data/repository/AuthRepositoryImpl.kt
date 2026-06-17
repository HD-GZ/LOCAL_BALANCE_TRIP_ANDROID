package live.lb_trip.data.repository

import live.lb_trip.data.datasource.local.TokenDataStore
import live.lb_trip.data.datasource.remote.AuthRemoteDataSource
import live.lb_trip.data.dto.request.EmailVerificationConfirmRequestDto
import live.lb_trip.data.dto.request.EmailVerificationResendRequestDto
import live.lb_trip.data.dto.request.LoginRequestDto
import live.lb_trip.data.dto.request.SignupRequestDto
import live.lb_trip.data.mapper.toDomain
import live.lb_trip.domain.exception.auth.LbTripAuthException
import live.lb_trip.domain.model.Gender
import live.lb_trip.domain.model.Tokens
import live.lb_trip.domain.model.User
import live.lb_trip.domain.repository.AuthRepository
import live.lb_trip.domain.util.mapApiFailure
import live.lb_trip.domain.util.suspendRunCatching
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
        birthDate: String,
        gender: Gender,
        termsAgreed: Boolean,
        privacyAgreed: Boolean,
        marketingAgreed: Boolean,
    ): Result<User> = suspendRunCatching {
        authRemoteDataSource.signup(
            SignupRequestDto(
                name = name,
                email = email,
                password = password,
                passwordConfirm = passwordConfirm,
                birthDate = birthDate,
                gender = gender.name,
                termsAgreed = termsAgreed,
                privacyAgreed = privacyAgreed,
                marketingAgreed = marketingAgreed,
            ),
        ).toDomain()
    }.mapApiFailure {
        on(400, "INVALID_INPUT_VALUE") { ex ->
            LbTripAuthException.InvalidInputValueException(fields = ex.fieldErrors.map { it.first })
        }
        on(400, "PASSWORD_CONFIRM_MISMATCH") throws LbTripAuthException.PasswordConfirmMismatchException()
        on(400, "REQUIRED_AGREEMENT_NOT_ACCEPTED") throws LbTripAuthException.RequiredAgreementNotAcceptedException()
        on(409, "DUPLICATE_EMAIL") throws LbTripAuthException.DuplicateEmailException()
    }

    override suspend fun login(email: String, password: String): Result<Tokens> =
        suspendRunCatching {
            val dto = authRemoteDataSource.login(LoginRequestDto(email = email, password = password))
            tokenDataStore.save(dto.accessToken, dto.refreshToken)
            Tokens(accessToken = dto.accessToken, refreshToken = dto.refreshToken)
        }.mapApiFailure {
            on(401, "INVALID_LOGIN_CREDENTIALS") throws LbTripAuthException.InvalidCredentialsException()
            on(403, "EMAIL_NOT_VERIFIED") throws LbTripAuthException.EmailNotVerifiedException()
            on(404, "USER_NOT_FOUND") throws LbTripAuthException.UserNotFoundException()
        }

    override suspend fun logout(): Result<Unit> =
        suspendRunCatching {
            authRemoteDataSource.logout()
        }.mapApiFailure {
            on(401) throws LbTripAuthException.UnauthorizedException()
        }

    override suspend fun resendEmailVerification(email: String): Result<User> =
        suspendRunCatching {
            authRemoteDataSource.resendEmailVerification(
                EmailVerificationResendRequestDto(email = email),
            ).toDomain()
        }.mapApiFailure {
            on(404, "USER_NOT_FOUND") throws LbTripAuthException.UserNotFoundException()
        }

    override suspend fun confirmEmailVerification(code: String): Result<User> =
        suspendRunCatching {
            authRemoteDataSource.confirmEmailVerification(
                EmailVerificationConfirmRequestDto(code = code),
            ).toDomain()
        }.mapApiFailure {
            on(400, "EMAIL_VERIFICATION_CODE_EXPIRED") throws LbTripAuthException.EmailVerificationCodeExpiredException()
            on(400, "EMAIL_VERIFICATION_CODE_USED") throws LbTripAuthException.EmailVerificationCodeUsedException()
            on(404, "EMAIL_VERIFICATION_CODE_NOT_FOUND") throws LbTripAuthException.EmailVerificationCodeNotFoundException()
        }
}
