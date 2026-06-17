package live.lb_trip.domain.usecase

import javax.inject.Inject
import live.lb_trip.domain.model.Gender
import live.lb_trip.domain.model.User
import live.lb_trip.domain.repository.AuthRepository

class SignupUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        passwordConfirm: String,
        birthDate: String,
        gender: Gender,
        termsAgreed: Boolean,
        privacyAgreed: Boolean,
        marketingAgreed: Boolean,
    ): Result<User> = authRepository.signup(
        name = name,
        email = email,
        password = password,
        passwordConfirm = passwordConfirm,
        birthDate = birthDate,
        gender = gender,
        termsAgreed = termsAgreed,
        privacyAgreed = privacyAgreed,
        marketingAgreed = marketingAgreed,
    )
}