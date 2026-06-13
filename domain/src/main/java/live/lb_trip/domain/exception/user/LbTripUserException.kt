package live.lb_trip.domain.exception.user

import live.lb_trip.domain.exception.LbTripException

sealed class LbTripUserException : LbTripException() {
    class EmailUnavailableException : LbTripUserException()
}
