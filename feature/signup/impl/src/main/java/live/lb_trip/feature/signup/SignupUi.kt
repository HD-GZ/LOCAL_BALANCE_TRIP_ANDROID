package live.lb_trip.feature.signup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.ui.Ui

class SignupUi : Ui<SignupState> {
    @Composable
    override fun Content(state: SignupState, modifier: Modifier) {
        Box(modifier = modifier.fillMaxSize())
    }
}
