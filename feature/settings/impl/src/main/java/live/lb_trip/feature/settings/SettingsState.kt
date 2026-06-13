package live.lb_trip.feature.settings

import com.slack.circuit.runtime.CircuitUiState

data class SettingsState(
    val title: String,
    val eventSink: (SettingsEvent) -> Unit = {}
) : CircuitUiState
