package live.lb_trip.feature.onboarding

import com.slack.circuit.runtime.CircuitUiState

data class OnboardingState(
    val eventSink: (OnboardingEvent) -> Unit = {},
) : CircuitUiState
