package live.lb_trip.data.mapper

import live.lb_trip.data.dto.response.TermsResponseDto
import live.lb_trip.domain.model.Terms

fun TermsResponseDto.toDomain(): Terms =
    Terms(
        title = title,
        version = version,
        effectiveDate = effectiveDate,
        content = content,
    )
