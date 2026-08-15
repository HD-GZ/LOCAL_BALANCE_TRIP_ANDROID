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
data class ShareTokenResponseDto(
    val token: String,
    val expiresAt: String,
)

@Serializable
data class SharedCourseDetailResponseDto(
    val savedCourseId: Long,
    val sharedByName: String,
    val imageUrl: String? = null,
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

@Serializable
data class ReceiptDetailResponseDto(
    val receiptId: Long,
    val merchantName: String,
    val amount: Int,
    val paidDate: String,
    val imageUrl: String,
)

@Serializable
data class ReceiptDownloadUrlResponseDto(
    val downloadUrl: String,
    val expiresAt: String,
)

@Serializable
data class SavedCourseReportResponseDto(
    val courseName: String,
    val imageUrl: String? = null,
    val visitedPlaceCount: Int,
    val durationMinutes: Long,
    val totalSpentAmount: Int,
    val tourEndedAt: String,
    val walkedDistanceMeters: Double? = null,
)

@Serializable
data class TourStartResponseDto(
    val tourStartedAt: String,
    val places: List<TourPlaceResponseDto>,
)

@Serializable
data class TourPlaceResponseDto(
    val placeId: Long,
    val order: Int,
    val visited: Boolean,
)

@Serializable
data class TourEndResponseDto(
    val completed: Boolean,
    val visitedPlaceCount: Int,
    val totalPlaceCount: Int,
    val durationMinutes: Long,
)
