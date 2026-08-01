package live.lb_trip.data.mapper

import live.lb_trip.data.dto.response.UserProfileResponseDto
import live.lb_trip.domain.model.Gender
import live.lb_trip.domain.model.UserProfile

fun UserProfileResponseDto.toDomain() = UserProfile(
    name = name,
    email = email,
    birthDate = birthDate.orEmpty(),
    gender = gender?.let { value -> Gender.entries.find { it.name == value } } ?: Gender.NOT_SPECIFIED,
)
