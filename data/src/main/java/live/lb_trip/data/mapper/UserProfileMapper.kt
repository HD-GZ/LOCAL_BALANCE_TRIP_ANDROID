package live.lb_trip.data.mapper

import live.lb_trip.data.dto.response.UserProfileResponseDto
import live.lb_trip.domain.model.UserProfile

fun UserProfileResponseDto.toDomain() = UserProfile(
    name = name,
    email = email,
)
