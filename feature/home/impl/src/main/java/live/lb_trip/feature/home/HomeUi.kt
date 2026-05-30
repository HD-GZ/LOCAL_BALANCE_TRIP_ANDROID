package live.lb_trip.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.slack.circuit.runtime.ui.Ui

class HomeUi : Ui<HomeState> {
    @Composable
    override fun Content(state: HomeState, modifier: Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = state.title,
                fontSize = 24.sp
            )

            Button(
                onClick = {
                    state.eventSink(HomeEvent.NavigateToSettings)
                }
            ) {
                Text("Navigate to Settings")
            }
        }
    }
}
