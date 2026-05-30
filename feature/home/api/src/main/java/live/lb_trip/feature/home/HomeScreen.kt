package live.lb_trip.feature.home

import android.os.Parcelable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
object HomeScreen : Screen, Parcelable

data class HomeState(
    val title: String,
    val eventSink: (HomeEvent) -> Unit = {}
) : CircuitUiState

sealed interface HomeEvent : CircuitUiEvent {
    data object NavigateToSettings : HomeEvent
}

