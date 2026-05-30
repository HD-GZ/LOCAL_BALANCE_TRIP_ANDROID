package live.lb_trip.domain.usecase

import javax.inject.Inject
import live.lb_trip.domain.repository.UserRepository

class UserLoginUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    operator fun invoke() {
        // TODO
    }
}
