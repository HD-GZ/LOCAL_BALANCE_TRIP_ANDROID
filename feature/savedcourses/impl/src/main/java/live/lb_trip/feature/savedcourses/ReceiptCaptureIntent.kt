package live.lb_trip.feature.savedcourses

sealed interface ReceiptCaptureIntent {
    data class ImagePicked(val bytes: ByteArray, val fileName: String) : ReceiptCaptureIntent
    data class MerchantNameChanged(val value: String) : ReceiptCaptureIntent
    data class AmountChanged(val value: String) : ReceiptCaptureIntent
    data class PaidDateChanged(val value: String) : ReceiptCaptureIntent
    data object SubmitClicked : ReceiptCaptureIntent
}
