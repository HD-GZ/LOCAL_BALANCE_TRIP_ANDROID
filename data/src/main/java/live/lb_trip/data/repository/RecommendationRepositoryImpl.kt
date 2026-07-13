package live.lb_trip.data.repository

import javax.inject.Inject
import live.lb_trip.data.datasource.remote.RecommendationRemoteDataSource
import live.lb_trip.data.mapper.toDomain
import live.lb_trip.domain.exception.recommendation.LbTripRecommendationException
import live.lb_trip.domain.model.CourseDetail
import live.lb_trip.domain.model.RecommendedCourse
import live.lb_trip.domain.model.RecommendedRegion
import live.lb_trip.domain.repository.RecommendationRepository
import live.lb_trip.domain.util.mapApiFailure
import live.lb_trip.domain.util.suspendRunCatching

class RecommendationRepositoryImpl @Inject constructor(
    private val recommendationRemoteDataSource: RecommendationRemoteDataSource,
) : RecommendationRepository {

    override suspend fun createRecommendations(): Result<Unit> =
        suspendRunCatching {
            recommendationRemoteDataSource.createRecommendations()
        }.mapApiFailure {
            on(404, "PROPENSITY_NOT_FOUND") throws LbTripRecommendationException.PropensityNotFoundException()
            on(503, "TOUR_API_UNAVAILABLE") throws LbTripRecommendationException.TourApiUnavailableException()
        }

    override suspend fun getRegions(): Result<List<RecommendedRegion>> =
        suspendRunCatching {
            recommendationRemoteDataSource.getRegions().map { it.toDomain() }
        }

    override suspend fun getRegionCourses(regionId: Long): Result<List<RecommendedCourse>> =
        suspendRunCatching {
            recommendationRemoteDataSource.getRegionCourses(regionId).map { it.toDomain() }
        }.mapApiFailure {
            on(404, "REGION_NOT_FOUND") throws LbTripRecommendationException.RegionNotFoundException()
        }

    override suspend fun getCourseDetail(courseId: Long): Result<CourseDetail> =
        suspendRunCatching {
            recommendationRemoteDataSource.getCourseDetail(courseId).toDomain()
        }.mapApiFailure {
            on(404, "COURSE_NOT_FOUND") throws LbTripRecommendationException.CourseNotFoundException()
        }

    override suspend fun saveCourse(courseId: Long): Result<Unit> =
        suspendRunCatching {
            recommendationRemoteDataSource.saveCourse(courseId)
        }.mapApiFailure {
            on(404, "COURSE_NOT_FOUND") throws LbTripRecommendationException.CourseNotFoundException()
        }
}
