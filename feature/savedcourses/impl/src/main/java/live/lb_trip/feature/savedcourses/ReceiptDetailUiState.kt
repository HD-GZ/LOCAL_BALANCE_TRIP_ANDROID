package live.lb_trip.feature.savedcourses

data class ReceiptDetailUiState(
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val merchantName: String = "",
    val amount: String = "",
    val paidDate: String = "",
    val imageUrl: String? = null,
    val editMerchantName: String = "",
    val editAmount: String = "",
    val editPaidDate: String = "",
)

sealed interface ReceiptDetailSideEffect {
    data object ShowLoadError : ReceiptDetailSideEffect
    data object ShowUpdateError : ReceiptDetailSideEffect
    data object ShowInvalidInput : ReceiptDetailSideEffect
    data object ShowUpdated : ReceiptDetailSideEffect
    data object ShowDeleteError : ReceiptDetailSideEffect
    data class DownloadUrlReady(val url: String) : ReceiptDetailSideEffect
    data object ShowDownloadError : ReceiptDetailSideEffect
    data object NavigateBackWithDeleted : ReceiptDetailSideEffect
}
