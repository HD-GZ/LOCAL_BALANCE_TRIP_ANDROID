package live.lb_trip.feature.tour.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.feature.tour.R
import live.lb_trip.feature.tour.TourStop

@Composable
internal fun TourBottomSheetContent(
    stops: ImmutableList<TourStop>,
    currentStopIndex: Int,
    progressStopIndex: Int,
    onEndTourClick: () -> Unit,
    onNextStopClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLastStop = progressStopIndex >= stops.lastIndex
    val displayedStop = if (isLastStop) stops.getOrNull(progressStopIndex) else stops.getOrNull(progressStopIndex + 1)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LbColors.Paper)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_route),
                contentDescription = null,
                tint = LbColors.Green,
                modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        if (isLastStop) {
                            R.string.tour_last_stop_label
                        } else {
                            R.string.tour_next_stop_label
                        },
                    ),
                    color = LbColors.Ink3,
                    fontSize = 11.sp,
                )
                Text(
                    text = displayedStop?.name.orEmpty(),
                    color = LbColors.Ink,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            LbButton(
                onClick = onEndTourClick,
                colors = LbButtonDefaults.whiteColors(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier,
            ) {
                Text(
                    text = stringResource(R.string.tour_end),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            LbButton(
                onClick = onNextStopClick,
                colors = LbButtonDefaults.greenColors(),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(
                        if (isLastStop) {
                            R.string.tour_finish
                        } else {
                            R.string.tour_next_stop_arrived
                        },
                    ),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp, modifier = Modifier.padding(top = 22.dp))

        Text(
            text = stringResource(R.string.tour_full_route),
            color = LbColors.Ink,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 18.dp, bottom = 6.dp),
        )

        TourRouteTimeline(stops = stops, currentStopIndex = currentStopIndex)

        Spacer(modifier = Modifier.height(24.dp))
    }
}
