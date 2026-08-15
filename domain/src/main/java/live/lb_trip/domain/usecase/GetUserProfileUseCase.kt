package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.UserProfile
import live.lb_trip.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Result<UserProfile> = userRepository.getMyProfile()
}
