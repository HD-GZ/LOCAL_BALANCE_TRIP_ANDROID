package live.lb_trip.domain.repository

import live.lb_trip.domain.model.CourseDetail
import live.lb_trip.domain.model.HeroItem
import live.lb_trip.domain.model.HomeFeedItem
import live.lb_trip.domain.model.PopularCourse
import live.lb_trip.domain.model.ProfileSummary
import live.lb_trip.domain.model.ProfileType
import live.lb_trip.domain.model.RegionIncentives

interface HomeRepository {
    suspend fun getHero(): Result<List<HeroItem>>

    suspend fun getProfileTypes(): Result<List<ProfileType>>

    suspend fun getProfileSummary(): Result<ProfileSummary>

    suspend fun getIncentives(): Result<List<RegionIncentives>>

    suspend fun getPopularCourses(): Result<List<PopularCourse>>

    suspend fun getPopularCourseDetail(courseId: Long): Result<CourseDetail>

    suspend fun getHomeFeed(): Result<List<HomeFeedItem>>
}
