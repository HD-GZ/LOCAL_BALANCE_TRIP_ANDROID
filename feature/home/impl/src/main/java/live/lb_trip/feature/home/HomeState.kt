package live.lb_trip.feature.home

import com.slack.circuit.runtime.CircuitUiState

data class HomeState(
    val eventSink: (HomeEvent) -> Unit = {}
) : CircuitUiState
