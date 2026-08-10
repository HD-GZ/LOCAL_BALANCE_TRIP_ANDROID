package live.lb_trip.feature.savedcourses

sealed interface ReceiptDetailIntent {
    data object Retry : ReceiptDetailIntent
    data object EditClicked : ReceiptDetailIntent
    data object EditCancelled : ReceiptDetailIntent
    data class MerchantNameChanged(val value: String) : ReceiptDetailIntent
    data class AmountChanged(val value: String) : ReceiptDetailIntent
    data class PaidDateChanged(val value: String) : ReceiptDetailIntent
    data object SaveClicked : ReceiptDetailIntent
    data object DownloadClicked : ReceiptDetailIntent
    data object DeleteClicked : ReceiptDetailIntent
    data object DeleteConfirmed : ReceiptDetailIntent
    data object DeleteDismissed : ReceiptDetailIntent
}
