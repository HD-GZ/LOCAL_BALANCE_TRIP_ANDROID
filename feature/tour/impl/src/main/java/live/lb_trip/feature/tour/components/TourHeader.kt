package live.lb_trip.feature.tour.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.feature.tour.R

private val StartedAtFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
internal fun TourHeader(
    regionName: String,
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LbTopBar(
        onBackClick = onBackClick,
        backContentDescription = stringResource(R.string.tour_back_content_description),
        title = title,
        subtitle = regionName.ifEmpty { null },
        modifier = modifier,
    )
}

@Composable
internal fun TourProgressSection(
    isFinished: Boolean,
    completedCount: Int,
    totalCount: Int,
    tourStartedAt: String?,
    elapsedMinutes: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LbColors.Paper)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = stringResource(
                    if (isFinished) R.string.tour_progress_finished_label else R.string.tour_progress_next_label,
                ),
                color = LbColors.Ink,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.tour_progress_template, completedCount, totalCount),
                color = LbColors.GreenForest,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        if (totalCount > 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 9.dp),
            ) {
                repeat(totalCount) { index ->
                    val color = when {
                        index < completedCount -> LbColors.Green
                        index == completedCount && !isFinished -> LbColors.GreenSoft
                        else -> LbColors.SurfaceSoft
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(color),
                    )
                }
            }
        }
        if (tourStartedAt != null) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.tour_progress_started_at, formatStartedAt(tourStartedAt)),
                    color = LbColors.Ink2,
                    fontSize = 10.sp,
                )
                Text(
                    text = stringResource(R.string.tour_progress_elapsed, formatElapsedMinutes(elapsedMinutes)),
                    color = LbColors.Ink2,
                    fontSize = 10.sp,
                )
            }
        }
    }
}

private fun formatStartedAt(tourStartedAt: String): String =
    runCatching { LocalDateTime.parse(tourStartedAt).format(StartedAtFormatter) }.getOrDefault("--:--")

@Composable
private fun formatElapsedMinutes(minutes: Int): String = if (minutes < MINUTES_PER_HOUR) {
    stringResource(R.string.tour_progress_elapsed_minutes, minutes)
} else {
    stringResource(R.string.tour_progress_elapsed_hours, minutes / MINUTES_PER_HOUR, minutes % MINUTES_PER_HOUR)
}

private const val MINUTES_PER_HOUR = 60
