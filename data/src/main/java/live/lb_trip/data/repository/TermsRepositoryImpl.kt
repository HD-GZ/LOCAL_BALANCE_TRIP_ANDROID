package live.lb_trip.data.repository

import live.lb_trip.data.datasource.remote.TermsRemoteDataSource
import live.lb_trip.data.mapper.toDomain
import live.lb_trip.domain.model.Terms
import live.lb_trip.domain.model.TermsType
import live.lb_trip.domain.repository.TermsRepository
import live.lb_trip.domain.util.suspendRunCatching
import javax.inject.Inject

class TermsRepositoryImpl
    @Inject
    constructor(
        private val termsRemoteDataSource: TermsRemoteDataSource,
    ) : TermsRepository {
        override suspend fun getTerms(type: TermsType): Result<Terms> =
            suspendRunCatching {
                termsRemoteDataSource.getTerms(type.toPathSegment()).toDomain()
            }
    }

private fun TermsType.toPathSegment(): String = when (this) {
    TermsType.SERVICE -> "service"
    TermsType.PRIVACY -> "privacy"
}
