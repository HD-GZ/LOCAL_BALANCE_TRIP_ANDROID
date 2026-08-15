package live.lb_trip.feature.savedcourses

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptDetailRoute(val savedCourseId: Long, val receiptId: Long)
