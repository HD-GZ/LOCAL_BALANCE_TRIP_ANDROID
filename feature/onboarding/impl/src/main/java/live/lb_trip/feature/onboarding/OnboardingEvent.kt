package live.lb_trip.feature.onboarding

import com.slack.circuit.runtime.CircuitUiEvent

sealed interface OnboardingEvent : CircuitUiEvent {
    data object NavigateToSignup : OnboardingEvent
    data object NavigateToSignin : OnboardingEvent
}
