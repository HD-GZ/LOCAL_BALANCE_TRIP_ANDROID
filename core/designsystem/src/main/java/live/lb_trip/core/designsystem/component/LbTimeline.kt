package live.lb_trip.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R

@Composable
fun LbTimeline(
    stops: List<LbTimelineStop>,
    expandedIndices: Set<Int>,
    audioGuideLabel: String,
    walkDurationLabel: @Composable (String) -> String,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
    detailHeader: @Composable (Int) -> Unit = {},
    audioContent: (@Composable (Int) -> Unit)? = null,
    detailExtraContent: (@Composable (Int) -> Unit)? = null,
    detailBottomPadding: Dp = 12.dp,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val nodeRadius = 13.5.dp.toPx()
            val bottomY = size.height - nodeRadius
            if (bottomY > nodeRadius) {
                drawLine(
                    color = LbColors.Line,
                    start = Offset(16.dp.toPx() + nodeRadius, nodeRadius),
                    end = Offset(16.dp.toPx() + nodeRadius, bottomY),
                    strokeWidth = 1.5.dp.toPx(),
                )
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            stops.forEachIndexed { index, stop ->
                LbTimelineStopBlock(
                    stop = stop,
                    isExpanded = index in expandedIndices,
                    showWalkLabel = stop.walkDuration != null && index != stops.lastIndex,
                    audioGuideLabel = audioGuideLabel,
                    walkDurationLabel = walkDurationLabel,
                    onToggle = { onToggle(index) },
                    detailHeader = { detailHeader(index) },
                    audioContent = audioContent?.let { content -> { content(index) } },
                    detailExtraContent = detailExtraContent?.let { content -> { content(index) } },
                    detailBottomPadding = detailBottomPadding,
                )
            }
        }
    }
}

@Composable
private fun LbTimelineStopBlock(
    stop: LbTimelineStop,
    isExpanded: Boolean,
    showWalkLabel: Boolean,
    audioGuideLabel: String,
    walkDurationLabel: @Composable (String) -> String,
    onToggle: () -> Unit,
    detailHeader: @Composable () -> Unit,
    audioContent: (@Composable () -> Unit)?,
    detailExtraContent: (@Composable () -> Unit)?,
    detailBottomPadding: Dp,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(27.dp)
                    .clip(CircleShape)
                    .background(LbColors.Paper)
                    .border(1.5.dp, LbColors.Green, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = stop.order.toString(), color = LbColors.Green, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = stop.name,
                color = LbColors.Ink,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = if (isExpanded) LbColors.Green else LbColors.Ink3,
                modifier = Modifier.size(16.dp).rotate(if (isExpanded) 90f else 0f),
            )
        }
        if (isExpanded) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.width(41.dp))
                Column(modifier = Modifier.weight(1f).padding(bottom = detailBottomPadding)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LbColors.Paper)
                            .border(1.dp, LbColors.LineSoft, RoundedCornerShape(12.dp)),
                    ) {
                        detailHeader()
                        Column(modifier = Modifier.padding(13.dp)) {
                            stop.description?.let { description ->
                                Text(text = description, color = LbColors.Ink2, fontSize = 11.5.sp, lineHeight = 17.sp)
                            }
                            detailExtraContent?.invoke()
                            if (stop.hasAudioGuide) {
                                Spacer(modifier = Modifier.padding(top = 9.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_headphone),
                                        contentDescription = null,
                                        tint = LbColors.Ink3,
                                        modifier = Modifier.size(15.dp),
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = audioGuideLabel, color = LbColors.Ink3, fontSize = 12.sp)
                                }
                                audioContent?.let { content ->
                                    Spacer(modifier = Modifier.padding(top = 9.dp))
                                    content()
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showWalkLabel) {
            stop.walkDuration?.let { walkDuration ->
                Row(
                    modifier = Modifier.padding(start = 41.dp, top = 6.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_walk),
                        contentDescription = null,
                        tint = LbColors.Ink4,
                        modifier = Modifier.size(13.dp),
                    )
                    Text(text = walkDurationLabel(walkDuration), color = LbColors.Ink3, fontSize = 10.5.sp)
                }
            }
        }
    }
}
