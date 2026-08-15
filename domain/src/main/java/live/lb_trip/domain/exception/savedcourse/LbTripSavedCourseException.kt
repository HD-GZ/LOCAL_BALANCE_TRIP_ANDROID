package live.lb_trip.domain.exception.savedcourse

import live.lb_trip.domain.exception.LbTripException

sealed class LbTripSavedCourseException : LbTripException() {
    class SavedCourseNotFoundException : LbTripSavedCourseException()

    class ImageNotFoundException : LbTripSavedCourseException()

    class ImageAlreadyAttachedException : LbTripSavedCourseException()

    class TourReportNotAvailableException : LbTripSavedCourseException()

    class SavedCoursePlaceNotFoundException : LbTripSavedCourseException()

    class TourReceiptNotFoundException : LbTripSavedCourseException()

    class TourAlreadyCompletedException : LbTripSavedCourseException()

    class TourNotInProgressException : LbTripSavedCourseException()

    class ShareTokenNotFoundException : LbTripSavedCourseException()

    class ShareTokenExpiredException : LbTripSavedCourseException()
}
