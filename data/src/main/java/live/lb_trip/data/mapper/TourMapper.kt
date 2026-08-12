package live.lb_trip.data.mapper

import live.lb_trip.data.dto.response.TourEndResponseDto
import live.lb_trip.data.dto.response.TourPlaceResponseDto
import live.lb_trip.data.dto.response.TourStartResponseDto
import live.lb_trip.domain.model.TourEndResult
import live.lb_trip.domain.model.TourPlaceVisit

fun TourStartResponseDto.toDomain(): List<TourPlaceVisit> = places.map { it.toDomain() }

fun TourPlaceResponseDto.toDomain(): TourPlaceVisit =
    TourPlaceVisit(placeId = placeId, order = order, visited = visited)

fun TourEndResponseDto.toDomain(): TourEndResult = TourEndResult(
    completed = completed,
    visitedPlaceCount = visitedPlaceCount,
    totalPlaceCount = totalPlaceCount,
    durationMinutes = durationMinutes,
)
