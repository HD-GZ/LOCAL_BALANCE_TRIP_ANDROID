package live.lb_trip.feature.tour.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.feature.tour.R
import live.lb_trip.feature.tour.TourUiState

private val ButtonVerticalPadding = 16.dp

@Composable
internal fun TourActionBar(
    state: TourUiState,
    onNextStopClick: () -> Unit,
    onFinishAcknowledged: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lastIndex = state.stops.lastIndex
    val nextIndex = (state.furthestStopIndex + 1).coerceIn(0, lastIndex.coerceAtLeast(0))
    val targetStop = state.stops.getOrNull(nextIndex)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LbColors.Paper)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        if (state.isFinished) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LbButton(
                    onClick = onFinishAcknowledged,
                    colors = LbButtonDefaults.whiteColors(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = ButtonVerticalPadding),
                ) {
                    Text(text = stringResource(R.string.tour_action_view_detail), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                LbButton(
                    onClick = onFinishAcknowledged,
                    colors = LbButtonDefaults.greenColors(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = ButtonVerticalPadding),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.tour_action_view_report), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            LbButton(
                onClick = onNextStopClick,
                colors = LbButtonDefaults.greenColors(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = ButtonVerticalPadding),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (state.furthestStopIndex >= lastIndex) {
                        stringResource(R.string.tour_finish)
                    } else {
                        stringResource(R.string.tour_next_stop_arrived, targetStop?.name.orEmpty())
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
