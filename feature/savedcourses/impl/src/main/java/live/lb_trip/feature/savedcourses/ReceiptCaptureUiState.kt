package live.lb_trip.feature.savedcourses

enum class ReceiptCaptureStep {
    PICK,
    SCANNING,
    VERIFY,
    SUBMITTING,
}

data class ReceiptCaptureUiState(
    val step: ReceiptCaptureStep = ReceiptCaptureStep.PICK,
    val imageId: Long? = null,
    val merchantName: String = "",
    val amount: String = "",
    val paidDate: String = "",
)

sealed interface ReceiptCaptureSideEffect {
    data object ShowScanError : ReceiptCaptureSideEffect
    data object ShowSubmitError : ReceiptCaptureSideEffect
    data object ShowInvalidInput : ReceiptCaptureSideEffect
    data object NavigateBackWithSuccess : ReceiptCaptureSideEffect
}
