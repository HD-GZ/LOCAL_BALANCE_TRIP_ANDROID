package live.lb_trip.domain.model

enum class TermsType { SERVICE, PRIVACY }

data class Terms(
    val title: String,
    val version: String,
    val effectiveDate: String,
    val content: String,
)
