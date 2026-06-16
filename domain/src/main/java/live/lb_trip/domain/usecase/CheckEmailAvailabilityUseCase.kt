package live.lb_trip.domain.usecase

import javax.inject.Inject
import live.lb_trip.domain.repository.UserRepository

class CheckEmailAvailabilityUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(email: String): Result<Boolean> =
        userRepository.checkEmailAvailability(email = email)
}
