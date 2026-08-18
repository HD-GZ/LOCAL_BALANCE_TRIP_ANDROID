package live.lb_trip.feature.tour.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import live.lb_trip.core.designsystem.component.LbBottomActionBar
import live.lb_trip.core.designsystem.component.LbBottomActionButton
import live.lb_trip.core.designsystem.component.LbBottomActionButtonRow
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.feature.tour.R
import live.lb_trip.feature.tour.TourUiState

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

    LbBottomActionBar(modifier = modifier) {
        if (state.isFinished) {
            LbBottomActionButtonRow {
                LbBottomActionButton(
                    text = stringResource(R.string.tour_action_view_detail),
                    onClick = onFinishAcknowledged,
                    colors = LbButtonDefaults.whiteColors(),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                LbBottomActionButton(
                    text = stringResource(R.string.tour_action_view_report),
                    onClick = onFinishAcknowledged,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        } else {
            LbBottomActionButton(
                text = if (state.furthestStopIndex >= lastIndex) {
                    stringResource(R.string.tour_finish)
                } else {
                    stringResource(R.string.tour_next_stop_arrived, targetStop?.name.orEmpty())
                },
                onClick = onNextStopClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
