package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.PasswordResetToken
import live.lb_trip.domain.repository.AuthRepository
import javax.inject.Inject

class ConfirmPasswordResetUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(
            email: String,
            code: String,
        ): Result<PasswordResetToken> = authRepository.confirmPasswordReset(email = email, code = code)
    }
