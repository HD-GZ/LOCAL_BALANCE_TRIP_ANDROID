package live.lb_trip.feature.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.usecase.GetHomeIncentivesUseCase

@HiltViewModel
class PolicyListViewModel @Inject constructor(
    private val getHomeIncentivesUseCase: GetHomeIncentivesUseCase,
) : BaseViewModel<PolicyListUiState, PolicyListIntent, PolicyListSideEffect>(PolicyListUiState()) {

    init {
        viewModelScope.launch { loadIncentives() }
    }

    override fun onIntent(intent: PolicyListIntent) {
        when (intent) {
            PolicyListIntent.Retry -> viewModelScope.launch { loadIncentives() }
            is PolicyListIntent.CardClicked -> postSideEffect(PolicyListSideEffect.OpenUrl(intent.card.url))
        }
    }

    private suspend fun loadIncentives() {
        updateState { it.copy(isLoading = true) }
        getHomeIncentivesUseCase()
            .onSuccess { regions -> updateState { it.copy(isLoading = false, cards = regions.toIncentiveCards()) } }
            .onFailure {
                updateState { it.copy(isLoading = false) }
                postSideEffect(PolicyListSideEffect.ShowLoadError)
            }
    }
}
