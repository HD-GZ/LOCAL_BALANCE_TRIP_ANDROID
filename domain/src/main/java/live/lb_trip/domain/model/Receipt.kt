package live.lb_trip.domain.model

data class Receipt(
    val receiptId: Long,
    val merchantName: String,
    val amount: Int,
    val paidDate: String,
)

data class ReceiptSummary(
    val totalAmount: Int,
    val receipts: List<Receipt>,
)

data class ReceiptScan(
    val imageId: Long,
    val imageUrl: String,
    val merchantName: String?,
    val amount: Int?,
    val paidDate: String?,
)
