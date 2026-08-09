package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.PasswordResetCodeIssued
import live.lb_trip.domain.repository.AuthRepository
import javax.inject.Inject

class RequestPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String): Result<PasswordResetCodeIssued> =
        authRepository.requestPasswordReset(email = email)
}
