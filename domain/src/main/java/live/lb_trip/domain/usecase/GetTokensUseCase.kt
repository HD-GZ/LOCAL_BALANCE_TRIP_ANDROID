package live.lb_trip.domain.usecase

import kotlinx.coroutines.flow.Flow
import live.lb_trip.domain.model.Tokens
import live.lb_trip.domain.repository.UserRepository
import javax.inject.Inject

class GetTokensUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Tokens?> = userRepository.getTokens()
}
