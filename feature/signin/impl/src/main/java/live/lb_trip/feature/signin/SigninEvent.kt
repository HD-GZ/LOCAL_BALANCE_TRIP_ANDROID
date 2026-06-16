package live.lb_trip.feature.signin

import com.slack.circuit.runtime.CircuitUiEvent

sealed interface SigninEvent : CircuitUiEvent {
    data object NavigateBack : SigninEvent
    data class UpdateEmail(val email: String) : SigninEvent
    data class UpdatePassword(val password: String) : SigninEvent
    data object TogglePasswordVisibility : SigninEvent
    data object Login : SigninEvent
    data object NavigateToForgotPassword : SigninEvent
    data object NavigateToSignup : SigninEvent
}
