package live.lb_trip.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class TermsResponseDto(
    val title: String,
    val version: String,
    val effectiveDate: String,
    val content: String,
)
