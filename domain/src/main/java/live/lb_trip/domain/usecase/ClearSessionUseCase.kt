package live.lb_trip.domain.usecase

import live.lb_trip.domain.repository.UserRepository
import javax.inject.Inject

class ClearSessionUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke() = userRepository.clearTokens()
    }
