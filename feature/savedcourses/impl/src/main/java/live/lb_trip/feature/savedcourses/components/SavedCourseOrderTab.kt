package live.lb_trip.feature.savedcourses.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbAudioPlayer
import live.lb_trip.core.designsystem.component.LbBenefitRow
import live.lb_trip.core.designsystem.component.LbTimeline
import live.lb_trip.core.designsystem.component.LbTimelineStop
import live.lb_trip.feature.savedcourses.R
import live.lb_trip.feature.savedcourses.SavedCourseDetailIntent
import live.lb_trip.feature.savedcourses.SavedCourseDetailUiState

@Composable
internal fun SavedCourseOrderTab(
    state: SavedCourseDetailUiState,
    onIntent: (SavedCourseDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LbTimeline(
            stops = state.stops.map {
                LbTimelineStop(
                    order = it.order,
                    name = it.name,
                    description = it.description,
                    walkDuration = it.walkDuration,
                    hasAudioGuide = it.hasAudioGuide,
                )
            },
            expandedIndices = state.expandedStopIndices,
            audioGuideLabel = stringResource(R.string.savedcourses_detail_audio_guide_label),
            walkDurationLabel = { walkDuration ->
                stringResource(R.string.savedcourses_detail_walk_time_template, walkDuration)
            },
            onToggle = { onIntent(SavedCourseDetailIntent.StopToggled(it)) },
            audioContent = { index ->
                LbAudioPlayer(
                    isPlaying = state.playingStopIndex == index,
                    onPlayPauseClick = { onIntent(SavedCourseDetailIntent.PlaybackToggled(index)) },
                    playContentDescription = stringResource(R.string.sharedcourse_content_description_play),
                    pauseContentDescription = stringResource(R.string.sharedcourse_content_description_pause),
                    positionLabel = stringResource(R.string.sharedcourse_audio_time_placeholder),
                )
            },
        )

        if (state.benefits.isNotEmpty()) {
            HorizontalDivider(
                color = LbColors.LineSoft,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            )
            Text(
                text = stringResource(R.string.savedcourses_detail_benefits_title),
                color = LbColors.Ink,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Column {
                state.benefits.fastForEachIndexed { index, benefit ->
                    if (index > 0) HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
                    LbBenefitRow(
                        title = benefit.title,
                        description = benefit.description,
                        onClick = { onIntent(SavedCourseDetailIntent.BenefitClicked(benefit.url)) },
                        horizontalPadding = 16.dp,
                    )
                }
            }
        }
    }
}
