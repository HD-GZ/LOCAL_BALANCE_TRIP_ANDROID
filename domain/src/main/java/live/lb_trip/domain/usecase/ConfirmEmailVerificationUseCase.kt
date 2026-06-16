package live.lb_trip.domain.usecase

import javax.inject.Inject
import live.lb_trip.domain.model.User
import live.lb_trip.domain.repository.AuthRepository

class ConfirmEmailVerificationUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(code: String): Result<User> =
        authRepository.confirmEmailVerification(code = code)
}
