package live.lb_trip.localbalancetrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import live.lb_trip.domain.exception.ApiException
import live.lb_trip.domain.usecase.GetTokensUseCase
import live.lb_trip.domain.usecase.GetUserProfileUseCase

@HiltViewModel
class MainViewModel @Inject constructor(
    getTokensUseCase: GetTokensUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
) : ViewModel() {
    val isLoggedIn: StateFlow<Boolean?> = getTokensUseCase()
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = null)

    private val _sessionExpiredEvent = Channel<Unit>(Channel.BUFFERED)
    val sessionExpiredEvent: Flow<Unit> = _sessionExpiredEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            if (isLoggedIn.filterNotNull().first()) validateSession()
        }
    }

    private suspend fun validateSession() {
        val exception = getUserProfileUseCase().exceptionOrNull()
        if (exception is ApiException && exception.statusCode == UNAUTHORIZED) {
            _sessionExpiredEvent.send(Unit)
        }
    }

    private companion object {
        const val UNAUTHORIZED = 401
    }
}
