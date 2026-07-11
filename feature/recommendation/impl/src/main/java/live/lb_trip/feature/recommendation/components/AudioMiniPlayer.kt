package live.lb_trip.feature.recommendation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import kotlin.math.PI
import kotlin.math.sin
import live.lb_trip.feature.recommendation.R

private val WaveformInactive = Color(0xFFA9CDB8)

@Composable
internal fun AudioMiniPlayer(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
    positionLabel: String? = null,
) {
    val playContentDescription = stringResource(R.string.recommendation_content_description_play)
    val pauseContentDescription = stringResource(R.string.recommendation_content_description_pause)
    val timePlaceholder = stringResource(R.string.recommendation_audio_time_placeholder)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(GreenTint)
            .border(1.dp, GreenLine, CircleShape)
            .padding(start = 7.dp, end = 13.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isPlaying) GreenDk else Green)
                .clickable(
                    onClickLabel = if (isPlaying) pauseContentDescription else playContentDescription,
                    role = Role.Button,
                    onClick = onPlayPauseClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(11.dp)) {
                if (isPlaying) {
                    val barWidth = size.width * 0.3f
                    drawRect(color = Color.White, topLeft = Offset.Zero, size = Size(barWidth, size.height))
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(size.width - barWidth, 0f),
                        size = Size(barWidth, size.height),
                    )
                } else {
                    val trianglePath = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, size.height / 2f)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(path = trianglePath, color = Color.White)
                }
            }
        }
        AudioWaveform(isPlaying = isPlaying, modifier = Modifier.weight(1f).height(24.dp))
        Text(
            text = positionLabel ?: timePlaceholder,
            color = Green,
            fontSize = 10.sp,
        )
    }
}

private val WaveformHeights = listOf(42, 68, 54, 88, 60, 34, 80, 50, 64, 44, 84, 56, 30, 72, 60, 92, 48, 40, 74, 55)

@Composable
private fun AudioWaveform(isPlaying: Boolean, modifier: Modifier = Modifier) {
    val phase = if (isPlaying) {
        val infiniteTransition = rememberInfiniteTransition(label = "waveform")
        val animatedPhase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * PI).toFloat(),
            animationSpec = infiniteRepeatable(animation = tween(durationMillis = 900, easing = LinearEasing)),
            label = "waveform-phase",
        )
        animatedPhase
    } else {
        0f
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WaveformHeights.fastForEachIndexed { index, height ->
            val isOn = !isPlaying && index < 7
            val pulse = if (isPlaying) 0.55f + 0.45f * ((sin(phase + index * 0.9f) + 1f) / 2f) else 1f
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(fraction = (height / 100f) * pulse)
                    .background(if (isPlaying || isOn) Green else WaveformInactive, RoundedCornerShape(2.dp)),
            )
        }
    }
}
