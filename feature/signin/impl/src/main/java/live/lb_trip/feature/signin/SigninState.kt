package live.lb_trip.feature.signin

import com.slack.circuit.runtime.CircuitUiState

data class SigninState(
    val eventSink: (SigninEvent) -> Unit = {}
) : CircuitUiState
