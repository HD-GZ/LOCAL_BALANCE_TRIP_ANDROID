package live.lb_trip.data.mapper

import live.lb_trip.data.dto.response.HomeFeedItemResponseDto
import live.lb_trip.data.dto.response.HomeFeedResponseDto
import live.lb_trip.data.dto.response.HomePopularCoursesResponseDto
import live.lb_trip.data.dto.response.PopularCourseDetailResponseDto
import live.lb_trip.data.dto.response.PopularCourseResponseDto
import live.lb_trip.domain.model.CourseDetail
import live.lb_trip.domain.model.HomeFeedItem
import live.lb_trip.domain.model.PopularCourse

fun HomePopularCoursesResponseDto.toDomain(): List<PopularCourse> = courses.map { it.toDomain() }

fun PopularCourseResponseDto.toDomain(): PopularCourse =
    PopularCourse(courseId = courseId, title = title, reason = reason, imageUrl = imageUrl, regionName = regionName)

fun HomeFeedResponseDto.toDomain(): List<HomeFeedItem> = items.map { it.toDomain() }

fun HomeFeedItemResponseDto.toDomain(): HomeFeedItem = when (itemType) {
    "SAVED_COURSE" -> HomeFeedItem.SavedCourseItem(id = id, title = title, imageUrl = imageUrl, subtitle = subtitle)
    else -> HomeFeedItem.RecommendedRegionItem(id = id, title = title, imageUrl = imageUrl, subtitle = subtitle)
}

fun PopularCourseDetailResponseDto.toDomain(): CourseDetail =
    CourseDetail(
        id = courseId,
        regionName = regionName,
        title = title,
        places = places.map { it.toDomain() },
        benefits = benefits.map { it.toDomain() },
    )
