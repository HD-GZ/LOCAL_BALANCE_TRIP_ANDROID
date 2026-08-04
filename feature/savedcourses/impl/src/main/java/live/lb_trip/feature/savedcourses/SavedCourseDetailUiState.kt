package live.lb_trip.feature.savedcourses

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import live.lb_trip.domain.model.TravelStatus

enum class SavedCourseDetailTab {
    COURSE,
    RECEIPT,
    REPORT,
}

data class SavedCourseDetailUiState(
    val isLoading: Boolean = true,
    val selectedTab: SavedCourseDetailTab = SavedCourseDetailTab.COURSE,
    val regionName: String = "",
    val title: String = "",
    val status: TravelStatus = TravelStatus.BEFORE_TRIP,
    val stops: ImmutableList<SavedCourseStop> = persistentListOf(),
    val expandedStopIndices: PersistentSet<Int> = persistentSetOf(0),
    val benefits: ImmutableList<SavedCourseBenefit> = persistentListOf(),
    val isReceiptsLoading: Boolean = false,
    val hasLoadedReceipts: Boolean = false,
    val receiptTotalAmount: Int = 0,
    val receipts: ImmutableList<SavedCourseReceipt> = persistentListOf(),
    val isReportLoading: Boolean = false,
    val isReportAvailable: Boolean = false,
    val reportImageUrl: String? = null,
    val reportVisitedPlaceCount: Int = 0,
    val reportTotalSpentAmount: Int = 0,
    val reportTourEndedAt: String = "",
    val reportDistanceWalkedMeters: Float? = null,
    val reportStepCount: Int? = null,
)

data class SavedCourseStop(
    val order: Int,
    val name: String,
    val hasAudioGuide: Boolean,
    val walkDuration: String?,
    val description: String?,
)

data class SavedCourseBenefit(
    val title: String,
    val description: String?,
    val url: String,
)

data class SavedCourseReceipt(
    val receiptId: Long,
    val merchantName: String,
    val amount: Int,
    val paidDate: String,
)

sealed interface SavedCourseDetailSideEffect {
    data class ShowLoadError(val reason: SavedCourseDetailLoadErrorReason) : SavedCourseDetailSideEffect
    data object ShowReceiptsLoadError : SavedCourseDetailSideEffect
    data object ShowReportLoadError : SavedCourseDetailSideEffect
    data class OpenBenefitUrl(val url: String) : SavedCourseDetailSideEffect
    data class NavigateToTour(val savedCourseId: Long) : SavedCourseDetailSideEffect
    data class NavigateToReceiptCapture(val savedCourseId: Long) : SavedCourseDetailSideEffect
}

enum class SavedCourseDetailLoadErrorReason {
    CourseNotFound,
    EmptyPlaces,
    Unknown,
}
