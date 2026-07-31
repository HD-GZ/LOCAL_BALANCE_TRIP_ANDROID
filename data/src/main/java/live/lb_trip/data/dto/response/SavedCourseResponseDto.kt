package live.lb_trip.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class SavedCourseListResponseDto(
    val totalCount: Long,
    val courses: List<SavedCourseResponseDto>,
)

@Serializable
data class SavedCourseResponseDto(
    val savedCourseId: Long,
    val courseName: String,
    val imageUrl: String? = null,
    val status: String,
)

@Serializable
data class SavedCourseDetailResponseDto(
    val savedCourseId: Long,
    val regionName: String,
    val title: String,
    val status: String,
    val places: List<PlaceResponseDto>,
    val benefits: List<BenefitResponseDto>,
)

@Serializable
data class BenefitResponseDto(
    val title: String,
    val description: String? = null,
    val url: String,
)

@Serializable
data class ReceiptSummaryResponseDto(
    val totalAmount: Int,
    val receipts: List<ReceiptResponseDto>,
)

@Serializable
data class ReceiptResponseDto(
    val receiptId: Long,
    val merchantName: String,
    val amount: Int,
    val paidDate: String,
)

@Serializable
data class ReceiptScanResponseDto(
    val imageId: Long,
    val imageUrl: String,
    val merchantName: String? = null,
    val amount: Int? = null,
    val paidDate: String? = null,
)
