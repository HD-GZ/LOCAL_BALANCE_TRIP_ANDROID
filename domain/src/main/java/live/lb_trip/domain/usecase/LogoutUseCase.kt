package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(): Result<Unit> = authRepository.logout()
    }
