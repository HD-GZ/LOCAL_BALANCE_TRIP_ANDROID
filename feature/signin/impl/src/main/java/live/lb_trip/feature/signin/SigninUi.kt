package live.lb_trip.feature.signin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.ui.Ui

class SigninUi : Ui<SigninState> {
    @Composable
    override fun Content(state: SigninState, modifier: Modifier) {
        Box(modifier = modifier.fillMaxSize())
    }
}
