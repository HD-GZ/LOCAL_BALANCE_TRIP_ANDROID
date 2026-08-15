package live.lb_trip.domain.usecase

import live.lb_trip.domain.model.Terms
import live.lb_trip.domain.model.TermsType
import live.lb_trip.domain.repository.TermsRepository
import javax.inject.Inject

class GetTermsUseCase @Inject constructor(
    private val termsRepository: TermsRepository,
) {
    suspend operator fun invoke(type: TermsType): Result<Terms> = termsRepository.getTerms(type)
}
