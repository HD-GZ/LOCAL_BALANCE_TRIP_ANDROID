package live.lb_trip.domain.usecase

import javax.inject.Inject
import live.lb_trip.domain.repository.AuthRepository

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> = authRepository.logout()
}
