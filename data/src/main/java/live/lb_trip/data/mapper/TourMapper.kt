package live.lb_trip.data.mapper

import live.lb_trip.data.dto.response.TourEndResponseDto
import live.lb_trip.data.dto.response.TourPlaceResponseDto
import live.lb_trip.data.dto.response.TourStartResponseDto
import live.lb_trip.domain.model.TourEndResult
import live.lb_trip.domain.model.TourPlaceVisit
import live.lb_trip.domain.model.TourStartResult

fun TourStartResponseDto.toDomain(): TourStartResult = TourStartResult(
    tourStartedAt = tourStartedAt,
    visits = places.map { it.toDomain() },
)

fun TourPlaceResponseDto.toDomain(): TourPlaceVisit =
    TourPlaceVisit(placeId = placeId, order = order, visited = visited)

fun TourEndResponseDto.toDomain(): TourEndResult = TourEndResult(
    completed = completed,
    visitedPlaceCount = visitedPlaceCount,
    totalPlaceCount = totalPlaceCount,
    durationMinutes = durationMinutes,
)
