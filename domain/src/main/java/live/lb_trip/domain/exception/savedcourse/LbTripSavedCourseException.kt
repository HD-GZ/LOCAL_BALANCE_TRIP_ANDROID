package live.lb_trip.domain.exception.savedcourse

import live.lb_trip.domain.exception.LbTripException

sealed class LbTripSavedCourseException : LbTripException() {
    class SavedCourseNotFoundException : LbTripSavedCourseException()

    class ImageNotFoundException : LbTripSavedCourseException()

    class ImageAlreadyAttachedException : LbTripSavedCourseException()
}
