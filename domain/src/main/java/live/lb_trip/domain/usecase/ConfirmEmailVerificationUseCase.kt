package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.User
import live.lb_trip.domain.repository.AuthRepository
import javax.inject.Inject

class ConfirmEmailVerificationUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(code: String): Result<User> = authRepository.confirmEmailVerification(code = code)
}
