package live.lb_trip.domain.usecase

import javax.inject.Inject
import live.lb_trip.domain.model.User
import live.lb_trip.domain.repository.AuthRepository

class ResendEmailVerificationUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String): Result<User> =
        authRepository.resendEmailVerification(email = email)
}
