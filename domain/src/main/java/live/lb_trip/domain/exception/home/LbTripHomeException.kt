package live.lb_trip.domain.exception.home

import live.lb_trip.domain.exception.LbTripException

sealed class LbTripHomeException : LbTripException() {
    class CourseNotFoundException : LbTripHomeException()
}
