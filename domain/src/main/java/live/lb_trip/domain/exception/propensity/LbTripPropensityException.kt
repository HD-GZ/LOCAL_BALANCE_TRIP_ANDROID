package live.lb_trip.domain.exception.propensity

import live.lb_trip.domain.exception.LbTripException

sealed class LbTripPropensityException : LbTripException() {
    class InvalidInputException : LbTripPropensityException()
}
