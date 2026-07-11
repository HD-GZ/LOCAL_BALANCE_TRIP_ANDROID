package live.lb_trip.feature.recommendation.components

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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.feature.recommendation.CourseStop
import live.lb_trip.feature.recommendation.R

private val PinColor = Color(0xFFAE5C48)

@Composable
internal fun Timeline(
    stops: ImmutableList<CourseStop>,
    expandedIndices: ImmutableSet<Int>,
    playingStopIndex: Int?,
    onToggle: (Int) -> Unit,
    onTogglePlayback: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val lineX = 16.dp.toPx() + 13.5.dp.toPx()
            val nodeRadius = 13.5.dp.toPx()
            val bottomY = size.height - nodeRadius
            if (bottomY > nodeRadius) {
                drawLine(
                    color = Line,
                    start = Offset(lineX, nodeRadius),
                    end = Offset(lineX, bottomY),
                    strokeWidth = 1.5.dp.toPx(),
                )
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            stops.fastForEachIndexed { index, stop ->
                val isLast = index == stops.lastIndex
                TimelineStopBlock(
                    stop = stop,
                    isExpanded = index in expandedIndices,
                    showWalkLabel = stop.walkDuration != null && !isLast,
                    isPlaying = playingStopIndex == index,
                    onToggle = { onToggle(index) },
                    onTogglePlayback = { onTogglePlayback(index) },
                )
            }
        }
    }
}

@Composable
private fun TimelineStopBlock(
    stop: CourseStop,
    isExpanded: Boolean,
    showWalkLabel: Boolean,
    isPlaying: Boolean,
    onToggle: () -> Unit,
    onTogglePlayback: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
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
                    .background(Paper)
                    .border(1.5.dp, Green, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = stop.order.toString(), color = Green, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = stop.name,
                color = Ink,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_chevron_right),
                contentDescription = null,
                tint = if (isExpanded) Green else Ink3,
                modifier = Modifier.size(16.dp).rotate(if (isExpanded) 90f else 0f),
            )
        }
        if (isExpanded) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.width(41.dp))
                Column(modifier = Modifier.weight(1f).padding(bottom = 7.dp)) {
                    TimelineStopDetail(stop = stop, isPlaying = isPlaying, onTogglePlayback = onTogglePlayback)
                }
            }
        }
        if (showWalkLabel && stop.walkDuration != null) {
            WalkTimeLabel(walkDuration = stop.walkDuration)
        }
    }
}

@Composable
private fun TimelineStopDetail(
    stop: CourseStop,
    isPlaying: Boolean,
    onTogglePlayback: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Paper)
            .border(1.dp, LineSoft, RoundedCornerShape(12.dp)),
    ) {
        MapPlaceholder()
        Column(modifier = Modifier.padding(13.dp)) {
            Text(text = stop.description, color = Ink2, fontSize = 11.5.sp, lineHeight = 17.sp)
            if (stop.hasAudioGuide) {
                Spacer(modifier = Modifier.height(9.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_headphone),
                        contentDescription = null,
                        tint = Ink3,
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(R.string.recommendation_audio_guide_label), color = Ink3, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(9.dp))
                AudioMiniPlayer(isPlaying = isPlaying, onPlayPauseClick = onTogglePlayback)
            }
        }
    }
}

@Composable
private fun MapPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(Brush.linearGradient(listOf(Color(0xFFEEF3EE), Color(0xFFE7EFE9)))),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(22.dp)) {
            val radius = size.minDimension / 2.6f
            val center = Offset(size.width / 2f, size.height / 2f)
            val pinPath = Path().apply {
                addOval(Rect(center = center, radius = radius))
                moveTo(center.x - radius * 0.62f, center.y + radius * 0.62f)
                lineTo(center.x, size.height)
                lineTo(center.x + radius * 0.62f, center.y + radius * 0.62f)
                close()
            }
            drawPath(path = pinPath, color = PinColor)
            drawCircle(color = Color.White, radius = radius * 0.42f, center = center)
        }
    }
}

@Composable
private fun WalkTimeLabel(walkDuration: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(start = 41.dp, top = 6.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_walk),
            contentDescription = null,
            tint = Ink4,
            modifier = Modifier.size(13.dp),
        )
        Text(text = stringResource(R.string.recommendation_walk_time_template, walkDuration), color = Ink3, fontSize = 10.5.sp)
    }
}
