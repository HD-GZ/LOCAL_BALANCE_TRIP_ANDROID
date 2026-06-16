package live.lb_trip.feature.signin

import com.slack.circuit.runtime.CircuitUiState

data class SigninState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val eventSink: (SigninEvent) -> Unit = {},
) : CircuitUiState
