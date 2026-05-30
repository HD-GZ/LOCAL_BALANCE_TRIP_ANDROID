package live.lb_trip.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.slack.circuit.runtime.ui.Ui

class SettingsUi : Ui<SettingsState> {
    @Composable
    override fun Content(state: SettingsState, modifier: Modifier) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.title,
                fontSize = 24.sp
            )
        }
    }
}
