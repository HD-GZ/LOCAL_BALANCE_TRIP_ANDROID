package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(
            resetToken: String,
            newPassword: String,
        ): Result<Unit> = authRepository.resetPassword(resetToken = resetToken, newPassword = newPassword)
    }
