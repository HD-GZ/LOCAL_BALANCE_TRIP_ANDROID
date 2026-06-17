package live.lb_trip.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.slack.circuit.runtime.ui.Ui

class HomeUi : Ui<HomeState> {
    @Composable
    override fun Content(state: HomeState, modifier: Modifier) {
        HomeScreen(modifier = modifier)
    }
}

@Composable
private fun HomeScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "홈")
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
