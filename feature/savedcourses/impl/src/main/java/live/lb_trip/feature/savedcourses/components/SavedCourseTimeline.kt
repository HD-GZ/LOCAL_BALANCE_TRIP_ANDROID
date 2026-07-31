package live.lb_trip.feature.savedcourses.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.feature.savedcourses.R
import live.lb_trip.feature.savedcourses.SavedCourseStop

@Composable
internal fun SavedCourseTimeline(
    stops: ImmutableList<SavedCourseStop>,
    expandedIndices: ImmutableSet<Int>,
    onToggle: (Int) -> Unit,
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
                SavedCourseTimelineStop(
                    stop = stop,
                    isExpanded = index in expandedIndices,
                    showWalkLabel = stop.walkDuration != null && !isLast,
                    onToggle = { onToggle(index) },
                )
            }
        }
    }
}

@Composable
private fun SavedCourseTimelineStop(
    stop: SavedCourseStop,
    isExpanded: Boolean,
    showWalkLabel: Boolean,
    onToggle: () -> Unit,
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
                Column(modifier = Modifier.weight(1f).padding(bottom = 12.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Paper)
                            .border(1.dp, LineSoft, RoundedCornerShape(12.dp))
                            .padding(13.dp),
                    ) {
                        Text(text = stop.description, color = Ink2, fontSize = 11.5.sp, lineHeight = 17.sp)
                        if (stop.hasAudioGuide) {
                            Spacer(modifier = Modifier.padding(top = 9.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_headphone),
                                    contentDescription = null,
                                    tint = Ink3,
                                    modifier = Modifier.size(15.dp),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.savedcourses_detail_audio_guide_label),
                                    color = Ink3,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showWalkLabel && stop.walkDuration != null) {
            Row(
                modifier = Modifier.padding(start = 41.dp, top = 6.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_walk),
                    contentDescription = null,
                    tint = Ink4,
                    modifier = Modifier.size(13.dp),
                )
                Text(
                    text = stringResource(R.string.savedcourses_detail_walk_time_template, stop.walkDuration),
                    color = Ink3,
                    fontSize = 10.5.sp,
                )
            }
        }
    }
}
