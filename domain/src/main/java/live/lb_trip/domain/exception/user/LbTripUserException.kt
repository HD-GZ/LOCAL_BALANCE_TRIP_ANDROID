package live.lb_trip.domain.exception.user

import live.lb_trip.domain.exception.LbTripException

sealed class LbTripUserException : LbTripException() {
    class EmailUnavailableException : LbTripUserException()

    class InvalidInputValueException(
        val fields: List<String>,
    ) : LbTripUserException()

    class UserNotFoundException : LbTripUserException()

    class UserWithdrawnException : LbTripUserException()
}
