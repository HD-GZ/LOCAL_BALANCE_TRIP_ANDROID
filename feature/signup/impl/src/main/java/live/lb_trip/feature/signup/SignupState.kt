package live.lb_trip.feature.signup

import com.slack.circuit.runtime.CircuitUiState

data class SignupState(
    val eventSink: (SignupEvent) -> Unit = {},
) : CircuitUiState
