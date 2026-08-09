package live.lb_trip.localbalancetrip

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.ApiException
import live.lb_trip.domain.usecase.GetTokensUseCase
import live.lb_trip.domain.usecase.GetUserProfileUseCase

data class MainUiState(
    val isLoggedIn: Boolean? = null,
)

sealed interface MainIntent

sealed interface MainSideEffect {
    data object ShowSessionExpired : MainSideEffect
}

@HiltViewModel
class MainViewModel @Inject constructor(
    getTokensUseCase: GetTokensUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
) : BaseViewModel<MainUiState, MainIntent, MainSideEffect>(MainUiState()) {

    init {
        viewModelScope.launch {
            getTokensUseCase()
                .map { it != null }
                .collect { isLoggedIn -> updateState { it.copy(isLoggedIn = isLoggedIn) } }
        }
        viewModelScope.launch {
            if (uiState.map { it.isLoggedIn }.filterNotNull().first()) validateSession()
        }
    }

    override fun onIntent(intent: MainIntent) = Unit

    private suspend fun validateSession() {
        val exception = getUserProfileUseCase().exceptionOrNull()
        if (exception is ApiException && exception.statusCode == UNAUTHORIZED) {
            postSideEffect(MainSideEffect.ShowSessionExpired)
        }
    }

    private companion object {
        const val UNAUTHORIZED = 401
    }
}
