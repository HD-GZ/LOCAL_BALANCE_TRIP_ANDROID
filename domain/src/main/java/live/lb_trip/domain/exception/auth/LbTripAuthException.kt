package live.lb_trip.domain.exception.auth

import live.lb_trip.domain.exception.LbTripException

sealed class LbTripAuthException : LbTripException() {
    class EmailAlreadyExistsException : LbTripAuthException()
    class InvalidCredentialsException : LbTripAuthException()
    class UserNotFoundException : LbTripAuthException()
    class EmailVerificationCodeExpiredException : LbTripAuthException()
    class EmailVerificationCodeInvalidException : LbTripAuthException()
    class UnauthorizedException : LbTripAuthException()
}
