package live.lb_trip.feature.settings

import live.lb_trip.domain.model.TermsType

sealed interface TermsIntent {
    data class Load(val type: TermsType) : TermsIntent
    data object Retry : TermsIntent
}
