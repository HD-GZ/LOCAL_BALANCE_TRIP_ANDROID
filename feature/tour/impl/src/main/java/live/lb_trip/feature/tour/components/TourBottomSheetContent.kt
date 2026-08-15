package live.lb_trip.feature.tour.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.core.designsystem.component.LbAudioPlayer
import live.lb_trip.core.designsystem.component.LbBenefitRow
import live.lb_trip.core.util.formatAudioPosition
import live.lb_trip.feature.tour.R
import live.lb_trip.feature.tour.TourBenefit
import live.lb_trip.feature.tour.TourStop

@Suppress("LongParameterList")
@Composable
internal fun TourBottomSheetContent(
    stops: ImmutableList<TourStop>,
    benefits: ImmutableList<TourBenefit>,
    currentStopIndex: Int,
    furthestStopIndex: Int,
    isFinished: Boolean,
    isAudioPlaying: Boolean,
    audioPositionMs: Int,
    audioDurationMs: Int,
    onStopClick: (Int) -> Unit,
    onPlaybackToggle: () -> Unit,
    onBenefitClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    bottomSpacerHeight: Dp = 24.dp,
) {
    val stop = stops.getOrNull(currentStopIndex) ?: return
    val lastIndex = stops.lastIndex
    val isVisited = currentStopIndex <= furthestStopIndex
    val nextIndex = (furthestStopIndex + 1).coerceIn(0, lastIndex)
    val isNextTarget = !isFinished && !isVisited && currentStopIndex == nextIndex

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LbColors.Paper)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            TourSheetHeaderRow(
                stop = stop,
                isVisited = isVisited,
                isNextTarget = isNextTarget,
                displayOrder = stop.order,
                totalCount = stops.size,
            )

            TourSheetDetailBody(
                stop = stop,
                stops = stops,
                benefits = benefits,
                currentStopIndex = currentStopIndex,
                furthestStopIndex = furthestStopIndex,
                isFinished = isFinished,
                isAudioPlaying = isAudioPlaying,
                audioPositionMs = audioPositionMs,
                audioDurationMs = audioDurationMs,
                onStopClick = onStopClick,
                onPlaybackToggle = onPlaybackToggle,
                onBenefitClick = onBenefitClick,
            )

            Spacer(modifier = Modifier.height(bottomSpacerHeight))
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun TourSheetHeaderRow(
    stop: TourStop,
    isVisited: Boolean,
    isNextTarget: Boolean,
    displayOrder: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isVisited) LbColors.GreenTint else LbColors.RequiredMark),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    if (isVisited) R.drawable.ic_check else DesignSystemR.drawable.ic_navigation,
                ),
                contentDescription = null,
                tint = if (isVisited) LbColors.Green else LbColors.Paper,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(modifier = Modifier.width(11.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = when {
                    isVisited -> stringResource(R.string.tour_sheet_status_visited)
                    isNextTarget -> stringResource(R.string.tour_sheet_status_next, displayOrder, totalCount)
                    else -> stringResource(R.string.tour_sheet_status_other, displayOrder)
                },
                color = LbColors.Ink2,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stop.name,
                color = LbColors.Ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            when {
                isVisited -> {
                    Text(stringResource(R.string.tour_sheet_recorded), color = LbColors.GreenForest, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.tour_sheet_order_label, displayOrder), color = LbColors.Ink2, fontSize = 9.5.sp)
                }
                stop.walkMinutesToNext != null -> {
                    Text(
                        stringResource(R.string.tour_sheet_walk_minutes_template, stop.walkMinutesToNext),
                        color = LbColors.GreenForest,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_walk),
                            contentDescription = null,
                            tint = LbColors.Ink2,
                            modifier = Modifier.size(11.dp),
                        )
                        Text(stringResource(R.string.tour_sheet_walk_label), color = LbColors.Ink2, fontSize = 9.5.sp)
                    }
                }
                else -> {
                    Text(stringResource(R.string.tour_sheet_departure), color = LbColors.GreenForest, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.tour_sheet_first_stop), color = LbColors.Ink2, fontSize = 9.5.sp)
                }
            }
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun TourSheetDetailBody(
    stop: TourStop,
    stops: ImmutableList<TourStop>,
    benefits: ImmutableList<TourBenefit>,
    currentStopIndex: Int,
    furthestStopIndex: Int,
    isFinished: Boolean,
    isAudioPlaying: Boolean,
    audioPositionMs: Int,
    audioDurationMs: Int,
    onStopClick: (Int) -> Unit,
    onPlaybackToggle: () -> Unit,
    onBenefitClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(14.dp))

        stop.description?.let { description ->
            Text(text = description, color = LbColors.Ink2, fontSize = 12.5.sp, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(14.dp))
        }

        val audioUrl = stop.audioUrl
        if (stop.hasAudioGuide && audioUrl != null) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_headphone),
                    contentDescription = null,
                    tint = LbColors.Ink2,
                    modifier = Modifier.size(15.dp),
                )
                Text(stringResource(R.string.tour_audio_guide_label), color = LbColors.Ink2, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(9.dp))
            LbAudioPlayer(
                isPlaying = isAudioPlaying,
                onPlayPauseClick = onPlaybackToggle,
                playContentDescription = stringResource(R.string.tour_audio_play_content_description),
                pauseContentDescription = stringResource(R.string.tour_audio_pause_content_description),
                positionLabel = formatAudioPosition(audioPositionMs, audioDurationMs),
            )
        } else {
            Text(
                text = stringResource(R.string.tour_audio_guide_empty),
                color = LbColors.Ink2,
                fontSize = 11.5.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LbColors.SurfaceSoft, RoundedCornerShape(11.dp))
                    .padding(horizontal = 13.dp, vertical = 11.dp),
            )
        }

        val firstBenefit = benefits.firstOrNull()
        if (firstBenefit != null && currentStopIndex == 0) {
            Spacer(modifier = Modifier.height(12.dp))
            LbBenefitRow(
                title = firstBenefit.title,
                description = firstBenefit.description,
                onClick = { onBenefitClick(firstBenefit.url) },
            )
        }

        HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp, modifier = Modifier.padding(top = 18.dp))

        Text(
            text = stringResource(R.string.tour_full_route),
            color = LbColors.Ink,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 18.dp, bottom = 6.dp),
        )

        TourRouteTimeline(
            stops = stops,
            furthestStopIndex = furthestStopIndex,
            isFinished = isFinished,
            onStopClick = onStopClick,
        )
    }
}
