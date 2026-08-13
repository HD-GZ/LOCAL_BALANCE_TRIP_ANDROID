package live.lb_trip.feature.tour.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import kotlinx.collections.immutable.ImmutableList
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.feature.tour.R
import live.lb_trip.feature.tour.TourStop

@Composable
internal fun TourRouteTimeline(
    stops: ImmutableList<TourStop>,
    furthestStopIndex: Int,
    isFinished: Boolean,
    onStopClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nextIndex = (furthestStopIndex + 1).coerceAtMost(stops.lastIndex.coerceAtLeast(0))
    Column(modifier = modifier.fillMaxWidth()) {
        stops.fastForEachIndexed { index, stop ->
            key("${stop.name}_${stop.latitude}_${stop.longitude}") {
                TourRouteRow(
                    stop = stop,
                    status = when {
                        index <= furthestStopIndex -> TourStopStatus.Completed
                        !isFinished && index == nextIndex -> TourStopStatus.Current
                        else -> TourStopStatus.Upcoming
                    },
                    showConnector = index != stops.lastIndex,
                    onClick = { onStopClick(index) },
                )
            }
        }
    }
}

@Composable
private fun TourRouteRow(
    stop: TourStop,
    status: TourStopStatus,
    showConnector: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min).clickable(onClick = onClick)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxHeight()) {
            TourStopBadge(order = stop.order, status = status)
            if (showConnector) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .width(1.5.dp)
                        .background(LbColors.Line),
                )
            }
        }
        Column(modifier = Modifier.padding(start = 12.dp, bottom = 14.dp)) {
            Text(
                text = stop.name,
                color = if (status == TourStopStatus.Upcoming) LbColors.Ink3 else LbColors.Ink,
                fontSize = 14.sp,
                fontWeight = if (status == TourStopStatus.Current) FontWeight.Bold else FontWeight.SemiBold,
            )
            if (stop.walkMinutesToNext != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 5.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_walk),
                        contentDescription = null,
                        tint = LbColors.Ink4,
                        modifier = Modifier.size(13.dp),
                    )
                    Text(
                        text = stringResource(R.string.tour_walk_time_template, "${stop.walkMinutesToNext}분"),
                        color = LbColors.Ink3,
                        fontSize = 10.5.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun TourStopBadge(order: Int, status: TourStopStatus, modifier: Modifier = Modifier) {
    val (background, borderColor, contentColor) = when (status) {
        TourStopStatus.Completed -> Triple(LbColors.Green, LbColors.Green, Color.White)
        TourStopStatus.Current -> Triple(LbColors.Paper, LbColors.Green, LbColors.Green)
        TourStopStatus.Upcoming -> Triple(LbColors.Paper, LbColors.Line, LbColors.Ink3)
    }
    Box(
        modifier = modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(background)
            .border(1.5.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (status == TourStopStatus.Completed) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp),
            )
        } else {
            Text(text = order.toString(), color = contentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
