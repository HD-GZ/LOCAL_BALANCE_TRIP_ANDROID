package live.lb_trip.feature.home

import com.slack.circuit.runtime.CircuitUiEvent

sealed interface HomeEvent : CircuitUiEvent {
    data object StartPropensityDiagnosis : HomeEvent
    data object NavigateToSettings : HomeEvent
}
