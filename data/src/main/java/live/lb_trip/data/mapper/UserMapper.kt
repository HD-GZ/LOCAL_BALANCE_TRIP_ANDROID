package live.lb_trip.data.mapper

import live.lb_trip.data.dto.response.UserResponseDto
import live.lb_trip.domain.model.User
import live.lb_trip.domain.model.UserStatus

fun UserResponseDto.toDomain() = User(
    userId = userId,
    email = email,
    status = UserStatus.valueOf(status),
)
