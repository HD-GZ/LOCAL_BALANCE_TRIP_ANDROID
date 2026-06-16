package live.lb_trip.feature.home

import com.slack.circuit.runtime.CircuitUiEvent

sealed interface HomeEvent : CircuitUiEvent {
    data object NavigateToSettings : HomeEvent
    data object NavigateToSignup : HomeEvent
    data object NavigateToLogin : HomeEvent
}
