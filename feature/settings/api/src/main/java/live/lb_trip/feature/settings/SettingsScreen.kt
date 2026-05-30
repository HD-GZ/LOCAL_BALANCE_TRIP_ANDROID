package live.lb_trip.feature.settings

import android.os.Parcelable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
object SettingsScreen : Screen, Parcelable

data class SettingsState(
    val title: String,
    val eventSink: (SettingsEvent) -> Unit = {}
) : CircuitUiState

sealed interface SettingsEvent : CircuitUiEvent
