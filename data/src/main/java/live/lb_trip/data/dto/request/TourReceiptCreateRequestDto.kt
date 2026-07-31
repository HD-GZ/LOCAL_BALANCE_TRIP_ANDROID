package live.lb_trip.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class TourReceiptCreateRequestDto(
    val imageId: Long,
    val merchantName: String,
    val amount: Int,
    val paidDate: String,
)
