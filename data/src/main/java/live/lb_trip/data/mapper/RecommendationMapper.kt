package live.lb_trip.data.mapper

import live.lb_trip.data.dto.response.CourseDetailResponseDto
import live.lb_trip.data.dto.response.CourseResponseDto
import live.lb_trip.data.dto.response.PlaceResponseDto
import live.lb_trip.data.dto.response.RegionResponseDto
import live.lb_trip.domain.model.CourseDetail
import live.lb_trip.domain.model.CoursePlace
import live.lb_trip.domain.model.RecommendedCourse
import live.lb_trip.domain.model.RecommendedRegion

fun RegionResponseDto.toDomain(): RecommendedRegion =
    RecommendedRegion(id = regionId, name = regionName, reason = reason)

fun CourseResponseDto.toDomain(): RecommendedCourse =
    RecommendedCourse(id = courseId, title = title, reason = reason, imageUrl = imageUrl)

fun CourseDetailResponseDto.toDomain(): CourseDetail =
    CourseDetail(
        id = courseId,
        regionName = regionName,
        title = title,
        places = places.map { it.toDomain() },
        benefits = benefits.map { it.toDomain() },
    )

fun PlaceResponseDto.toDomain(): CoursePlace =
    CoursePlace(
        order = order,
        name = name,
        description = description,
        imageUrl = imageUrl,
        longitude = longitude,
        latitude = latitude,
        walkMinutes = walkMinutes,
        hasAudio = hasAudio,
        audioUrl = audioUrl,
    )
