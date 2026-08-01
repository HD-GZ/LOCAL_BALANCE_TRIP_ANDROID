package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.Gender
import live.lb_trip.domain.model.UserProfile
import live.lb_trip.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(
            name: String,
            birthDate: String,
            gender: Gender,
            password: String?,
            passwordConfirm: String?,
        ): Result<UserProfile> =
            userRepository.updateProfile(
                name = name,
                birthDate = birthDate,
                gender = gender,
                password = password,
                passwordConfirm = passwordConfirm,
            )
    }
