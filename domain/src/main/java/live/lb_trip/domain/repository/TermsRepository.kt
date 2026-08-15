package live.lb_trip.domain.repository

import live.lb_trip.domain.model.Terms
import live.lb_trip.domain.model.TermsType

interface TermsRepository {
    suspend fun getTerms(type: TermsType): Result<Terms>
}
