package live.lb_trip.domain.usecase

import javax.inject.Inject
import live.lb_trip.domain.model.Tokens
import live.lb_trip.domain.repository.AuthRepository

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<Tokens> =
        authRepository.login(email = email, password = password)
}
