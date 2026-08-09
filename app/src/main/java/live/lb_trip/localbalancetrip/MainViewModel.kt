package live.lb_trip.localbalancetrip

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.ApiException
import live.lb_trip.domain.usecase.GetTokensUseCase
import live.lb_trip.domain.usecase.GetUserProfileUseCase

data class MainUiState(
    val isLoggedIn: Boolean? = null,
)

sealed interface MainIntent {
    data class TokensChanged(val isLoggedIn: Boolean) : MainIntent
    data object SessionValidationRequested : MainIntent
}

sealed interface MainSideEffect {
    data object ShowSessionExpired : MainSideEffect
}

@HiltViewModel
class MainViewModel @Inject constructor(
    getTokensUseCase: GetTokensUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
) : BaseViewModel<MainUiState, MainIntent, MainSideEffect>(MainUiState()) {

    private var hasResolvedInitialToken = false
    private var sessionValidationJob: Job? = null

    init {
        viewModelScope.launch {
            getTokensUseCase()
                .map { it != null }
                .collect { isLoggedIn -> onIntent(MainIntent.TokensChanged(isLoggedIn)) }
        }
    }

    override fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.TokensChanged -> handleTokensChanged(intent)
            MainIntent.SessionValidationRequested -> validateSession()
        }
    }

    private fun handleTokensChanged(intent: MainIntent.TokensChanged) {
        updateState { it.copy(isLoggedIn = intent.isLoggedIn) }
        if (!intent.isLoggedIn) {
            sessionValidationJob?.cancel()
            sessionValidationJob = null
            return
        }
        if (!hasResolvedInitialToken) {
            hasResolvedInitialToken = true
            onIntent(MainIntent.SessionValidationRequested)
        }
    }

    private fun validateSession() {
        sessionValidationJob?.cancel()
        sessionValidationJob = viewModelScope.launch {
            val exception = getUserProfileUseCase().exceptionOrNull()
            if (exception is ApiException && exception.statusCode == UNAUTHORIZED) {
                postSideEffect(MainSideEffect.ShowSessionExpired)
            }
        }
    }

    private companion object {
        const val UNAUTHORIZED = 401
    }
}
