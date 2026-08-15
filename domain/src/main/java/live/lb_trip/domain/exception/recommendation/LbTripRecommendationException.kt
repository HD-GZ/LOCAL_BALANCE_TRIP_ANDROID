package live.lb_trip.domain.exception.recommendation

import live.lb_trip.domain.exception.LbTripException

sealed class LbTripRecommendationException : LbTripException() {
    class PropensityNotFoundException : LbTripRecommendationException()

    class TourApiUnavailableException : LbTripRecommendationException()

    class RegionNotFoundException : LbTripRecommendationException()

    class CourseNotFoundException : LbTripRecommendationException()
}
