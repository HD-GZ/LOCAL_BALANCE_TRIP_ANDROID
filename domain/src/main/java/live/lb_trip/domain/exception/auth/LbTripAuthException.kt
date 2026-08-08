package live.lb_trip.domain.exception.auth

import live.lb_trip.domain.exception.LbTripException

sealed class LbTripAuthException : LbTripException() {
    class InvalidInputValueException(
        val fields: List<String>,
    ) : LbTripAuthException()

    class DuplicateEmailException : LbTripAuthException()

    class PasswordConfirmMismatchException : LbTripAuthException()

    class RequiredAgreementNotAcceptedException : LbTripAuthException()

    class InvalidCredentialsException : LbTripAuthException()

    class EmailNotVerifiedException : LbTripAuthException()

    class UserNotFoundException : LbTripAuthException()

    class EmailVerificationCodeExpiredException : LbTripAuthException()

    class EmailVerificationCodeUsedException : LbTripAuthException()

    class EmailVerificationCodeNotFoundException : LbTripAuthException()

    class UnauthorizedException : LbTripAuthException()

    class UserWithdrawnException : LbTripAuthException()

    class PasswordResetCodeExpiredException : LbTripAuthException()

    class PasswordResetCodeUsedException : LbTripAuthException()

    class PasswordResetCodeNotFoundException : LbTripAuthException()

    class PasswordResetTokenExpiredException : LbTripAuthException()

    class PasswordResetTokenUsedException : LbTripAuthException()

    class PasswordResetTokenNotFoundException : LbTripAuthException()
}
