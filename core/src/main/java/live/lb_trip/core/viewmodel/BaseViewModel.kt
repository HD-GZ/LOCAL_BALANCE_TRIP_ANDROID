package live.lb_trip.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Orbit-MVI-style base: a single [State] StateFlow plus a one-shot [SideEffect] channel,
 * without pulling in the Orbit-MVI library itself.
 */
abstract class BaseViewModel<State, SideEffect>(initialState: State) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    protected val currentState: State
        get() = _uiState.value

    protected fun updateState(transform: (State) -> State) {
        _uiState.update(transform)
    }

    private val _sideEffect = Channel<SideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<SideEffect> = _sideEffect.receiveAsFlow()

    protected fun postSideEffect(sideEffect: SideEffect) {
        viewModelScope.launch { _sideEffect.send(sideEffect) }
    }
}
