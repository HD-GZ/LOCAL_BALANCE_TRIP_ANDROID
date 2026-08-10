package live.lb_trip.data.repository

import javax.inject.Inject
import live.lb_trip.data.datasource.remote.HomeRemoteDataSource
import live.lb_trip.data.mapper.toDomain
import live.lb_trip.domain.exception.home.LbTripHomeException
import live.lb_trip.domain.exception.propensity.LbTripPropensityException
import live.lb_trip.domain.model.CourseDetail
import live.lb_trip.domain.model.HeroItem
import live.lb_trip.domain.model.HomeFeedItem
import live.lb_trip.domain.model.PopularCourse
import live.lb_trip.domain.model.ProfileSummary
import live.lb_trip.domain.model.ProfileType
import live.lb_trip.domain.model.RegionIncentives
import live.lb_trip.domain.repository.HomeRepository
import live.lb_trip.domain.util.mapApiFailure
import live.lb_trip.domain.util.suspendRunCatching

class HomeRepositoryImpl @Inject constructor(
    private val homeRemoteDataSource: HomeRemoteDataSource,
) : HomeRepository {

    override suspend fun getHero(): Result<List<HeroItem>> =
        suspendRunCatching { homeRemoteDataSource.getHero().toDomain() }

    override suspend fun getProfileTypes(): Result<List<ProfileType>> =
        suspendRunCatching { homeRemoteDataSource.getProfileTypes().toDomain() }

    override suspend fun getProfileSummary(): Result<ProfileSummary> =
        suspendRunCatching {
            homeRemoteDataSource.getProfileSummary().toDomain()
        }.mapApiFailure {
            on(404, "PROPENSITY_NOT_FOUND") throws LbTripPropensityException.PropensityNotFoundException()
        }

    override suspend fun getIncentives(): Result<List<RegionIncentives>> =
        suspendRunCatching { homeRemoteDataSource.getIncentives().toDomain() }

    override suspend fun getPopularCourses(): Result<List<PopularCourse>> =
        suspendRunCatching { homeRemoteDataSource.getPopularCourses().toDomain() }

    override suspend fun getPopularCourseDetail(courseId: Long): Result<CourseDetail> =
        suspendRunCatching {
            homeRemoteDataSource.getPopularCourseDetail(courseId).toDomain()
        }.mapApiFailure {
            on(404, "COURSE_NOT_FOUND") throws LbTripHomeException.CourseNotFoundException()
        }

    override suspend fun getHomeFeed(): Result<List<HomeFeedItem>> =
        suspendRunCatching { homeRemoteDataSource.getHomeFeed().toDomain() }
}
